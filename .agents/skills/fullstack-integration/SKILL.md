---
name: fullstack-integration
description: Frontend-Backend integration patterns for React TypeScript + Kotlin Spring Boot multi-module projects. This skill should be used when working on API contracts, type synchronization, authentication flows, error handling across layers, or any task that bridges frontend and backend boundaries. Triggers on tasks involving API client generation, DTO-to-TypeScript mapping, OAuth2/JWT token management, cross-layer error handling, or environment-specific configuration.
metadata:
  author: jin
  version: "1.0.0"
---

# Fullstack Integration Patterns

Comprehensive guide for seamless React TypeScript ↔ Kotlin Spring Boot integration in multi-module projects. Contains 48 rules across 6 categories, focused on type safety, contract-first development, and consistent error handling across the full stack.

## When to Apply

Reference these guidelines when:
- Defining or modifying API endpoints that both frontend and backend consume
- Generating TypeScript clients from OpenAPI specs
- Implementing authentication/authorization flows (OAuth2, JWT)
- Mapping backend errors to frontend error handling
- Configuring environment-specific API endpoints
- Setting up API interceptors, retry logic, or caching on the frontend
- Synchronizing data models between Kotlin DTOs and TypeScript types

## Rule Categories by Priority

| Priority | Category | Impact | Prefix |
|----------|----------|--------|--------|
| 1 | API Contract & Code Generation | CRITICAL | `contract-` |
| 2 | Type Synchronization | CRITICAL | `type-` |
| 3 | Authentication & Authorization | HIGH | `auth-` |
| 4 | Error Handling Across Layers | HIGH | `error-` |
| 5 | API Client Patterns | MEDIUM-HIGH | `client-` |
| 6 | Environment & Configuration | MEDIUM | `env-` |

---

## Detailed Rules

### 1. API Contract & Code Generation (CRITICAL)

#### `contract-openapi-source-of-truth`
**Generate OpenAPI spec from backend code, use it to generate frontend clients**

The backend owns the API contract. Never manually write TypeScript API types.

❌ Bad: Manually duplicated types
```typescript
// frontend - manually written, drifts from backend
interface AuctionResponse {
  id: number;
  title: string;
  price: number; // was renamed to currentPrice on backend last week
}
```

✅ Good: Contract-first with code generation
```kotlin
// backend - Spring Boot controller with OpenAPI annotations
@Operation(summary = "Get auction details")
@ApiResponse(
    responseCode = "200",
    description = "Auction found",
    content = [Content(schema = Schema(implementation = AuctionResponse::class))]
)
@GetMapping("/api/v1/auctions/{id}")
suspend fun getAuction(@PathVariable id: Long): AuctionResponse
```

```yaml
# build pipeline: backend generates spec → frontend consumes
# backend/build.gradle.kts
plugins {
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

openApi {
    outputDir.set(file("$buildDir/openapi"))
    outputFileName.set("openapi.json")
}
```

```json
// frontend/package.json
{
  "scripts": {
    "generate-api": "openapi-typescript ../backend/build/openapi/openapi.json -o src/api/generated/schema.d.ts",
    "generate-client": "orval --config orval.config.ts"
  }
}
```

**Why it matters**: Manual type duplication is the #1 source of frontend-backend integration bugs. A single source of truth eliminates entire categories of runtime errors.

---

#### `contract-orval-client-generation`
**Use Orval or openapi-typescript for type-safe API client generation**

❌ Bad: Manual fetch wrappers
```typescript
// Fragile, no type safety on request/response
async function getAuction(id: number) {
  const res = await fetch(`/api/v1/auctions/${id}`);
  return res.json(); // any type!
}
```

✅ Good: Generated client with full type safety
```typescript
// orval.config.ts
export default defineConfig({
  api: {
    input: "../backend/build/openapi/openapi.json",
    output: {
      mode: "tags-split",
      target: "src/api/generated",
      client: "react-query",
      override: {
        mutator: {
          path: "src/api/custom-instance.ts",
          name: "customInstance",
        },
        query: {
          useQuery: true,
          useMutation: true,
        },
      },
    },
  },
});
```

```typescript
// Auto-generated hook usage - fully typed
import { useGetAuction } from "@/api/generated/auctions";

function AuctionDetail({ id }: { id: number }) {
  const { data, isLoading, error } = useGetAuction(id);
  // data is fully typed as AuctionResponse
}
```

---

#### `contract-api-versioning-consistent`
**Use consistent API versioning across frontend and backend**

✅ Good: Path-based versioning with centralized config
```kotlin
// backend - version in path
@RestController
@RequestMapping("/api/v1/auctions")
class AuctionController(...)
```

```typescript
// frontend - centralized API version
// src/api/config.ts
export const API_CONFIG = {
  version: "v1",
  baseUrl: `${import.meta.env.VITE_API_BASE_URL}/api/v1`,
} as const;
```

---

### 2. Type Synchronization (CRITICAL)

#### `type-dto-mapping-strategy`
**Define clear DTO → TypeScript mapping conventions**

❌ Bad: Inconsistent naming between layers
```kotlin
// backend DTO
data class AuctionDetailResponse(
    val auctionId: Long,          // camelCase
    val current_price: BigDecimal, // mixed snake_case!
    val created_at: Instant
)
```

✅ Good: Consistent conventions with Jackson config
```kotlin
// backend - global Jackson config ensures consistent serialization
@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper = jacksonObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}

// DTO - consistently camelCase
data class AuctionDetailResponse(
    val auctionId: Long,
    val currentPrice: BigDecimal,
    val createdAt: Instant  // serialized as ISO-8601 string
)
```

```typescript
// frontend - generated type matches exactly
interface AuctionDetailResponse {
  auctionId: number;
  currentPrice: number;
  createdAt: string; // ISO-8601, parse with dayjs/date-fns
}
```

---

#### `type-enum-sync`
**Synchronize enums between Kotlin and TypeScript**

❌ Bad: Duplicated enum definitions that drift
```kotlin
// backend
enum class AuctionStatus { DRAFT, ACTIVE, CLOSED, CANCELLED }
```
```typescript
// frontend - manually maintained, missing CANCELLED
type AuctionStatus = "DRAFT" | "ACTIVE" | "CLOSED";
```

✅ Good: Enums generated from OpenAPI spec
```kotlin
// backend - enum serialized as string by default with Jackson
enum class AuctionStatus {
    DRAFT, ACTIVE, CLOSED, CANCELLED
}
```

```typescript
// Auto-generated from OpenAPI spec
export const AuctionStatus = {
  DRAFT: "DRAFT",
  ACTIVE: "ACTIVE",
  CLOSED: "CLOSED",
  CANCELLED: "CANCELLED",
} as const;
export type AuctionStatus = (typeof AuctionStatus)[keyof typeof AuctionStatus];
```

---

#### `type-datetime-handling`
**Use ISO-8601 strings over the wire, parse on frontend**

✅ Good: Consistent datetime handling
```kotlin
// backend - always ISO-8601
data class AuctionResponse(
    val createdAt: Instant,    // → "2024-01-15T09:30:00Z"
    val closingDate: LocalDate // → "2024-02-15"
)
```

```typescript
// frontend - parse at the boundary
import { parseISO, format } from "date-fns";
import { ko } from "date-fns/locale";

function formatDateTime(iso: string): string {
  return format(parseISO(iso), "yyyy년 MM월 dd일 HH:mm", { locale: ko });
}
```

---

#### `type-pagination-response`
**Standardize pagination response structure across all endpoints**

✅ Good: Shared pagination types
```kotlin
// backend - generic cursor-based pagination wrapper
data class CursorPage<T>(
    val items: List<T>,
    val nextCursor: String?,
    val hasNext: Boolean,
    val totalCount: Long? = null
)
```

```typescript
// frontend - generated or manually defined once
interface CursorPage<T> {
  items: T[];
  nextCursor: string | null;
  hasNext: boolean;
  totalCount?: number;
}

// Usage with React Query infinite query
function useAuctionList() {
  return useInfiniteQuery({
    queryKey: ["auctions"],
    queryFn: ({ pageParam }) => getAuctions({ cursor: pageParam }),
    getNextPageParam: (lastPage) =>
      lastPage.hasNext ? lastPage.nextCursor : undefined,
  });
}
```

---

### 3. Authentication & Authorization (HIGH)

#### `auth-oauth2-token-flow`
**Implement token refresh transparently via API interceptor**

✅ Good: Centralized token management
```typescript
// src/api/custom-instance.ts
import axios, { AxiosError, InternalAxiosRequestConfig } from "axios";
import { tokenStorage } from "@/auth/token-storage";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

// Attach access token to every request
apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = tokenStorage.getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 with token refresh
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (token: string) => void;
  reject: (error: unknown) => void;
}> = [];

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config;
    if (error.response?.status !== 401 || !originalRequest) {
      return Promise.reject(error);
    }

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject });
      }).then((token) => {
        originalRequest.headers.Authorization = `Bearer ${token}`;
        return apiClient(originalRequest);
      });
    }

    isRefreshing = true;
    try {
      const newToken = await refreshAccessToken();
      tokenStorage.setAccessToken(newToken);
      failedQueue.forEach(({ resolve }) => resolve(newToken));
      failedQueue = [];
      originalRequest.headers.Authorization = `Bearer ${newToken}`;
      return apiClient(originalRequest);
    } catch (refreshError) {
      failedQueue.forEach(({ reject }) => reject(refreshError));
      failedQueue = [];
      tokenStorage.clear();
      window.location.href = "/login";
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  }
);

export const customInstance = apiClient;
```

---

#### `auth-backend-security-config`
**Configure Spring Security for SPA with stateless JWT**

✅ Good: Stateless security for API-first architecture
```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/auth/**").permitAll()
                it.requestMatchers("/api/v1/public/**").permitAll()
                it.requestMatchers("/actuator/health").permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->
                    response.status = 401
                    response.contentType = "application/problem+json"
                    response.writer.write("""
                        {"type":"/problems/unauthorized","title":"Unauthorized","status":401}
                    """.trimIndent())
                }
            }
            .build()
}
```

---

#### `auth-route-guard-pattern`
**Protect frontend routes with consistent auth guards**

✅ Good: Route-level auth with redirect
```typescript
// src/auth/ProtectedRoute.tsx
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "@/auth/useAuth";

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: string;
}

export function ProtectedRoute({ children, requiredRole }: ProtectedRouteProps) {
  const { isAuthenticated, user, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) return <LoadingSkeleton />;

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (requiredRole && !user?.roles.includes(requiredRole)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <>{children}</>;
}
```

---

### 4. Error Handling Across Layers (HIGH)

#### `error-problem-detail-mapping`
**Map backend RFC 7807 ProblemDetail to frontend error types**

✅ Good: Typed error handling across the stack
```kotlin
// backend - sealed class errors → ProblemDetail response
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ResponseEntity<ProblemDetail> {
        val problem = when (ex) {
            is AuctionNotFound -> ProblemDetail.forStatus(404).apply {
                type = URI("/problems/auction-not-found")
                title = "Auction Not Found"
                detail = "Auction ${ex.auctionId} does not exist"
                setProperty("auctionId", ex.auctionId.value)
            }
            is BidTooLow -> ProblemDetail.forStatus(422).apply {
                type = URI("/problems/bid-too-low")
                title = "Bid Too Low"
                detail = "Minimum bid is ${ex.minimum}"
                setProperty("minimum", ex.minimum)
                setProperty("offered", ex.offered)
            }
        }
        return ResponseEntity.status(problem.status).body(problem)
    }
}
```

```typescript
// frontend - typed error parsing
interface ProblemDetail {
  type: string;
  title: string;
  status: number;
  detail?: string;
  instance?: string;
  [key: string]: unknown; // extension fields
}

// Discriminated union for known error types
type ApiError =
  | { type: "/problems/auction-not-found"; auctionId: number }
  | { type: "/problems/bid-too-low"; minimum: number; offered: number }
  | { type: "/problems/unauthorized" }
  | { type: "unknown"; status: number; detail?: string };

function parseApiError(error: unknown): ApiError {
  if (axios.isAxiosError(error) && error.response?.data?.type) {
    const problem = error.response.data as ProblemDetail;
    switch (problem.type) {
      case "/problems/auction-not-found":
        return { type: problem.type, auctionId: problem.auctionId as number };
      case "/problems/bid-too-low":
        return {
          type: problem.type,
          minimum: problem.minimum as number,
          offered: problem.offered as number,
        };
      default:
        return { type: "unknown", status: problem.status, detail: problem.detail };
    }
  }
  return { type: "unknown", status: 500 };
}
```

---

#### `error-validation-display`
**Map backend validation errors to form field errors**

✅ Good: Structured validation error mapping
```kotlin
// backend - validation errors with field mapping
@ExceptionHandler(MethodArgumentNotValidException::class)
fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ProblemDetail> {
    val problem = ProblemDetail.forStatus(400).apply {
        type = URI("/problems/validation-error")
        title = "Validation Error"
        setProperty("errors", ex.bindingResult.fieldErrors.map { fieldError ->
            mapOf(
                "field" to fieldError.field,
                "message" to fieldError.defaultMessage,
                "rejected" to fieldError.rejectedValue
            )
        })
    }
    return ResponseEntity.badRequest().body(problem)
}
```

```typescript
// frontend - map to react-hook-form errors
import { UseFormSetError, FieldPath, FieldValues } from "react-hook-form";

interface ValidationError {
  field: string;
  message: string;
  rejected: unknown;
}

function applyServerErrors<T extends FieldValues>(
  errors: ValidationError[],
  setError: UseFormSetError<T>
) {
  errors.forEach(({ field, message }) => {
    setError(field as FieldPath<T>, {
      type: "server",
      message,
    });
  });
}

// Usage in mutation error handler
const mutation = useMutation({
  mutationFn: createAuction,
  onError: (error) => {
    const apiError = parseApiError(error);
    if (apiError.type === "/problems/validation-error") {
      applyServerErrors(apiError.errors, form.setError);
    } else {
      toast.error(apiError.detail ?? "An error occurred");
    }
  },
});
```

---

#### `error-toast-notification-pattern`
**Centralize API error display with toast notifications**

✅ Good: Global error handler with toast
```typescript
// src/api/error-handler.ts
import { toast } from "sonner";

const ERROR_MESSAGES: Record<string, string> = {
  "/problems/auction-not-found": "해당 경매를 찾을 수 없습니다.",
  "/problems/bid-too-low": "입찰 금액이 너무 낮습니다.",
  "/problems/unauthorized": "로그인이 필요합니다.",
};

export function handleApiError(error: unknown): void {
  const parsed = parseApiError(error);
  const message = ERROR_MESSAGES[parsed.type] ?? parsed.detail ?? "오류가 발생했습니다.";
  toast.error(message);
}

// Global setup with React Query
const queryClient = new QueryClient({
  defaultOptions: {
    mutations: {
      onError: handleApiError,
    },
  },
});
```

---

### 5. API Client Patterns (MEDIUM-HIGH)

#### `client-react-query-conventions`
**Standardize React Query key and hook patterns**

✅ Good: Consistent query key factory
```typescript
// src/api/query-keys.ts
export const auctionKeys = {
  all: ["auctions"] as const,
  lists: () => [...auctionKeys.all, "list"] as const,
  list: (filters: AuctionFilters) => [...auctionKeys.lists(), filters] as const,
  details: () => [...auctionKeys.all, "detail"] as const,
  detail: (id: number) => [...auctionKeys.details(), id] as const,
};

// Usage - consistent cache invalidation
const mutation = useMutation({
  mutationFn: placeBid,
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: auctionKeys.detail(auctionId) });
    queryClient.invalidateQueries({ queryKey: auctionKeys.lists() });
  },
});
```

---

#### `client-optimistic-updates`
**Use optimistic updates for responsive UI**

✅ Good: Optimistic mutation with rollback
```typescript
const placeBidMutation = useMutation({
  mutationFn: placeBid,
  onMutate: async (newBid) => {
    await queryClient.cancelQueries({ queryKey: auctionKeys.detail(newBid.auctionId) });

    const previousAuction = queryClient.getQueryData<AuctionDetail>(
      auctionKeys.detail(newBid.auctionId)
    );

    queryClient.setQueryData<AuctionDetail>(
      auctionKeys.detail(newBid.auctionId),
      (old) => old ? { ...old, currentPrice: newBid.amount, bidCount: old.bidCount + 1 } : old
    );

    return { previousAuction };
  },
  onError: (_err, newBid, context) => {
    if (context?.previousAuction) {
      queryClient.setQueryData(
        auctionKeys.detail(newBid.auctionId),
        context.previousAuction
      );
    }
  },
  onSettled: (_data, _err, newBid) => {
    queryClient.invalidateQueries({ queryKey: auctionKeys.detail(newBid.auctionId) });
  },
});
```

---

### 6. Environment & Configuration (MEDIUM)

#### `env-api-endpoint-management`
**Centralize API base URL per environment**

✅ Good: Environment-aware API configuration
```typescript
// .env.development
VITE_API_BASE_URL=http://localhost:8080
VITE_OAUTH_REDIRECT_URI=http://localhost:5173/auth/callback

// .env.staging
VITE_API_BASE_URL=https://api-staging.example.com
VITE_OAUTH_REDIRECT_URI=https://staging.example.com/auth/callback

// .env.production
VITE_API_BASE_URL=https://api.example.com
VITE_OAUTH_REDIRECT_URI=https://example.com/auth/callback
```

```typescript
// src/config/env.ts - type-safe env access
const envSchema = z.object({
  VITE_API_BASE_URL: z.string().url(),
  VITE_OAUTH_REDIRECT_URI: z.string().url(),
});

export const env = envSchema.parse(import.meta.env);
```

---

#### `env-cors-configuration`
**Configure CORS correctly for each environment**

✅ Good: Environment-specific CORS
```kotlin
// backend - application-{profile}.yml
@Configuration
class CorsConfig(
    @Value("\${app.cors.allowed-origins}")
    private val allowedOrigins: List<String>
) {
    @Bean
    fun corsConfigurer(): WebMvcConfigurer = object : WebMvcConfigurer {
        override fun addCorsMappings(registry: CorsRegistry) {
            registry.addMapping("/api/**")
                .allowedOrigins(*allowedOrigins.toTypedArray())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600)
        }
    }
}
```

```yaml
# application-local.yml
app:
  cors:
    allowed-origins:
      - http://localhost:5173
      - http://localhost:3000

# application-prod.yml
app:
  cors:
    allowed-origins:
      - https://example.com
```

---

#### `env-proxy-dev-server`
**Configure frontend dev server proxy to avoid CORS in development**

✅ Good: Vite proxy config
```typescript
// vite.config.ts
export default defineConfig({
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
```

---

## Anti-Patterns Cheat Sheet

| Anti-Pattern | Rule | Fix |
|-------------|------|-----|
| Manual TypeScript API types | `contract-openapi-source-of-truth` | Generate from OpenAPI spec |
| Raw fetch with `any` return | `contract-orval-client-generation` | Use generated typed client |
| Mixed camelCase/snake_case DTOs | `type-dto-mapping-strategy` | Global Jackson naming strategy |
| Duplicated enum definitions | `type-enum-sync` | Generate from spec |
| Token handling in every component | `auth-oauth2-token-flow` | Centralized interceptor |
| `catch (e) { alert(e.message) }` | `error-problem-detail-mapping` | Typed ProblemDetail parsing |
| Hardcoded `localhost:8080` | `env-api-endpoint-management` | Env-specific configuration |
| CORS errors in dev | `env-proxy-dev-server` | Vite proxy config |
