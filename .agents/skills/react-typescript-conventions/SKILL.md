---
name: react-typescript-conventions
description: React TypeScript component conventions and architectural patterns for frontend development. This skill should be used when writing React components, defining TypeScript types for props and state, structuring feature directories, implementing form handling, managing client vs server state, or setting up error boundaries. Triggers on tasks involving React component creation, TypeScript strict mode patterns, Tanstack Query integration, React Hook Form + Zod validation, or frontend project structure decisions.
metadata:
  author: jin
  version: "1.0.0"
---

# React TypeScript Conventions

Comprehensive guide for writing consistent, type-safe, and maintainable React TypeScript code. Contains 45 rules across 6 categories, covering component patterns, TypeScript idioms, state management, form handling, error boundaries, and project structure.

## When to Apply

Reference these guidelines when:
- Creating new React components or pages
- Defining TypeScript types for props, state, or API responses
- Choosing state management approach (server state vs client state)
- Implementing form validation with React Hook Form + Zod
- Structuring feature directories in the frontend project
- Setting up error boundaries and suspense patterns
- Writing custom hooks or utility functions

## Rule Categories by Priority

| Priority | Category | Impact | Prefix |
|----------|----------|--------|--------|
| 1 | TypeScript Patterns | CRITICAL | `ts-` |
| 2 | Component Structure | CRITICAL | `comp-` |
| 3 | State Management | HIGH | `state-` |
| 4 | Form Handling & Validation | HIGH | `form-` |
| 5 | Error Boundaries & Suspense | MEDIUM-HIGH | `error-` |
| 6 | Project Structure | MEDIUM | `dir-` |

---

## Detailed Rules

### 1. TypeScript Patterns (CRITICAL)

#### `ts-discriminated-union-props`
**Use discriminated unions for component variant props**

❌ Bad: Boolean prop explosion
```typescript
interface ButtonProps {
  variant?: "primary" | "secondary" | "danger";
  isLoading?: boolean;
  isDisabled?: boolean;
  icon?: ReactNode;
  iconPosition?: "left" | "right";
  // What happens when isLoading AND isDisabled? Undefined behavior.
}
```

✅ Good: Discriminated union for mutually exclusive states
```typescript
type ButtonProps = {
  children: ReactNode;
  onClick?: () => void;
} & (
  | { variant: "primary"; destructive?: never }
  | { variant: "secondary"; destructive?: never }
  | { variant: "danger"; destructive: true }
);

type ButtonState =
  | { status: "idle" }
  | { status: "loading"; progress?: number }
  | { status: "disabled"; reason: string };

interface SmartButtonProps extends ButtonProps {
  state: ButtonState;
}
```

---

#### `ts-generic-components`
**Use generics for reusable data-driven components**

❌ Bad: Losing type safety with `any`
```typescript
interface SelectProps {
  options: Array<{ label: string; value: any }>;
  onChange: (value: any) => void;
}
```

✅ Good: Generic component preserves type information
```typescript
interface SelectProps<T extends string | number> {
  options: Array<{ label: string; value: T }>;
  value: T;
  onChange: (value: T) => void;
  placeholder?: string;
}

function Select<T extends string | number>({
  options,
  value,
  onChange,
  placeholder,
}: SelectProps<T>) {
  return (
    <select
      value={value}
      onChange={(e) => onChange(e.target.value as T)}
    >
      {placeholder && <option value="">{placeholder}</option>}
      {options.map((opt) => (
        <option key={opt.value} value={opt.value}>
          {opt.label}
        </option>
      ))}
    </select>
  );
}

// Usage - fully typed
<Select<AuctionStatus>
  options={statusOptions}
  value={selectedStatus}
  onChange={setSelectedStatus}  // (value: AuctionStatus) => void
/>
```

---

#### `ts-strict-event-handlers`
**Type event handlers explicitly, never use `any`**

❌ Bad: Untyped event handlers
```typescript
const handleChange = (e: any) => {
  setName(e.target.value);
};
```

✅ Good: Properly typed handlers
```typescript
const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
  setName(e.target.value);
};

const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
  e.preventDefault();
  // ...
};

// For custom events or callbacks
type OnFilterChange = (filters: AuctionFilters) => void;
```

---

#### `ts-const-assertion-literals`
**Use `as const` for literal type inference**

❌ Bad: Widened types
```typescript
const ROUTES = {
  HOME: "/",
  AUCTIONS: "/auctions",
  AUCTION_DETAIL: "/auctions/:id",
};
// typeof ROUTES.HOME → string (too wide)
```

✅ Good: Const assertion for narrow types
```typescript
const ROUTES = {
  HOME: "/",
  AUCTIONS: "/auctions",
  AUCTION_DETAIL: "/auctions/:id",
} as const;
// typeof ROUTES.HOME → "/" (exact literal)

type Route = (typeof ROUTES)[keyof typeof ROUTES];
// "/" | "/auctions" | "/auctions/:id"
```

---

#### `ts-branded-types`
**Use branded types for domain IDs to prevent ID mixup**

This mirrors the Kotlin `@JvmInline value class` pattern on the frontend.

❌ Bad: Raw number IDs can be confused
```typescript
function getAuction(auctionId: number, userId: number) { ... }
getAuction(userId, auctionId); // Compiles! Bug at runtime.
```

✅ Good: Branded types for compile-time safety
```typescript
type Brand<T, B extends string> = T & { readonly __brand: B };

type AuctionId = Brand<number, "AuctionId">;
type UserId = Brand<number, "UserId">;

function auctionId(id: number): AuctionId {
  return id as AuctionId;
}

function getAuction(auctionId: AuctionId, userId: UserId) { ... }
// getAuction(userId, auctionId) → TypeScript error!
```

---

#### `ts-exhaustive-switch`
**Ensure exhaustive handling with `never` type**

✅ Good: Compile-time exhaustiveness check
```typescript
function assertNever(value: never): never {
  throw new Error(`Unexpected value: ${value}`);
}

function getStatusColor(status: AuctionStatus): string {
  switch (status) {
    case "DRAFT": return "gray";
    case "ACTIVE": return "green";
    case "CLOSED": return "blue";
    case "CANCELLED": return "red";
    default: return assertNever(status);
    // If a new status is added, TypeScript errors here
  }
}
```

---

### 2. Component Structure (CRITICAL)

#### `comp-single-responsibility`
**One component, one concern — split container and presentational**

❌ Bad: Component does fetching + logic + rendering
```typescript
function AuctionPage() {
  const [auction, setAuction] = useState(null);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    fetch(`/api/auctions/${id}`)
      .then(res => res.json())
      .then(data => { setAuction(data); setLoading(false); });
  }, [id]);

  if (loading) return <Spinner />;
  return (
    <div>
      <h1>{auction.title}</h1>
      {/* 200 lines of mixed rendering and logic */}
    </div>
  );
}
```

✅ Good: Separated concerns
```typescript
// Container: data fetching + orchestration
function AuctionPage() {
  const { id } = useParams<{ id: string }>();
  const { data: auction, isLoading } = useGetAuction(Number(id));

  if (isLoading) return <AuctionSkeleton />;
  if (!auction) return <NotFound resource="auction" />;

  return <AuctionDetail auction={auction} />;
}

// Presentational: pure rendering
interface AuctionDetailProps {
  auction: AuctionResponse;
}

function AuctionDetail({ auction }: AuctionDetailProps) {
  return (
    <article>
      <AuctionHeader title={auction.title} status={auction.status} />
      <PriceDisplay price={auction.currentPrice} />
      <BidHistory bids={auction.recentBids} />
    </article>
  );
}
```

---

#### `comp-named-exports`
**Use named exports, avoid default exports**

❌ Bad: Default exports cause inconsistent naming
```typescript
// AuctionCard.tsx
export default function AuctionCard() { ... }
// Importer can name it anything: import Banana from "./AuctionCard"
```

✅ Good: Named exports enforce consistency
```typescript
// AuctionCard.tsx
export function AuctionCard({ auction }: AuctionCardProps) { ... }
// Import must use correct name: import { AuctionCard } from "./AuctionCard"
```

Exception: page-level components for file-based routing (Next.js) require default exports.

---

#### `comp-hook-extraction`
**Extract complex logic into custom hooks**

✅ Good: Custom hook encapsulates related logic
```typescript
// hooks/useAuctionBid.ts
export function useAuctionBid(auctionId: AuctionId) {
  const queryClient = useQueryClient();
  const { data: auction } = useGetAuction(auctionId);

  const placeBid = useMutation({
    mutationFn: (amount: number) =>
      api.auctions.placeBid(auctionId, { amount }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: auctionKeys.detail(auctionId) });
      toast.success("입찰이 완료되었습니다.");
    },
    onError: handleApiError,
  });

  const minimumBid = auction
    ? auction.currentPrice * 1.05
    : 0;

  return {
    placeBid: placeBid.mutate,
    isPlacing: placeBid.isPending,
    minimumBid,
    currentPrice: auction?.currentPrice,
  };
}

// Component stays clean
function BidForm({ auctionId }: { auctionId: AuctionId }) {
  const { placeBid, isPlacing, minimumBid } = useAuctionBid(auctionId);
  // Simple rendering logic only
}
```

---

#### `comp-children-over-props`
**Prefer children and composition over prop drilling**

❌ Bad: Prop drilling through multiple levels
```typescript
<Layout
  header={<Header />}
  sidebar={<Sidebar items={menuItems} />}
  footer={<Footer />}
  mainContent={<AuctionList auctions={auctions} />}
  errorBanner={error && <ErrorBanner error={error} />}
/>
```

✅ Good: Composition with children and slots
```typescript
function Layout({ children }: { children: ReactNode }) {
  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <div className="flex flex-1">
        <Sidebar />
        <main className="flex-1 p-6">{children}</main>
      </div>
      <Footer />
    </div>
  );
}

// Usage — clear and composable
<Layout>
  <AuctionList auctions={auctions} />
</Layout>
```

---

### 3. State Management (HIGH)

#### `state-server-vs-client`
**Separate server state (React Query) from client state (useState/Zustand)**

Server state: data from the backend (cached, refetched, invalidated).
Client state: UI state (modals, filters, form inputs, theme).

❌ Bad: Mixing server data in client state
```typescript
function AuctionList() {
  const [auctions, setAuctions] = useState([]);      // Server data in client state
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({});         // Client state

  useEffect(() => {
    fetchAuctions(filters).then(data => {
      setAuctions(data);
      setLoading(false);
    });
  }, [filters]);
}
```

✅ Good: React Query for server state, useState for UI state
```typescript
function AuctionList() {
  const [filters, setFilters] = useState<AuctionFilters>({});  // Client state
  const { data: auctions, isLoading } = useAuctionList(filters); // Server state

  return (
    <>
      <AuctionFilters value={filters} onChange={setFilters} />
      {isLoading ? <AuctionListSkeleton /> : <AuctionGrid auctions={auctions} />}
    </>
  );
}
```

---

#### `state-zustand-for-global-ui`
**Use Zustand for global client state that spans multiple components**

✅ Good: Zustand store for cross-component UI state
```typescript
// stores/useUIStore.ts
import { create } from "zustand";

interface UIState {
  sidebarOpen: boolean;
  toggleSidebar: () => void;
  theme: "light" | "dark";
  setTheme: (theme: "light" | "dark") => void;
}

export const useUIStore = create<UIState>((set) => ({
  sidebarOpen: true,
  toggleSidebar: () => set((s) => ({ sidebarOpen: !s.sidebarOpen })),
  theme: "light",
  setTheme: (theme) => set({ theme }),
}));

// Usage — no prop drilling needed
function Header() {
  const toggleSidebar = useUIStore((s) => s.toggleSidebar);
  return <button onClick={toggleSidebar}>Toggle</button>;
}
```

---

#### `state-url-as-state`
**Use URL search params as state for filterable/shareable views**

✅ Good: URL-driven state for shareable views
```typescript
import { useSearchParams } from "react-router-dom";

function useAuctionFilters() {
  const [searchParams, setSearchParams] = useSearchParams();

  const filters: AuctionFilters = {
    category: searchParams.get("category") ?? undefined,
    minPrice: searchParams.get("minPrice") ? Number(searchParams.get("minPrice")) : undefined,
    sort: (searchParams.get("sort") as SortOption) ?? "recent",
  };

  const setFilters = (newFilters: Partial<AuctionFilters>) => {
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev);
      Object.entries(newFilters).forEach(([key, value]) => {
        if (value != null) next.set(key, String(value));
        else next.delete(key);
      });
      return next;
    });
  };

  return { filters, setFilters };
}
```

---

### 4. Form Handling & Validation (HIGH)

#### `form-zod-schema-first`
**Define Zod schema first, derive TypeScript type from it**

❌ Bad: Separate type and validation logic
```typescript
interface CreateAuctionForm {
  title: string;
  description: string;
  startingPrice: number;
}
// Validation logic scattered in component
```

✅ Good: Zod schema as single source of truth
```typescript
import { z } from "zod";

export const createAuctionSchema = z.object({
  title: z
    .string()
    .min(2, "제목은 2자 이상이어야 합니다")
    .max(100, "제목은 100자 이하여야 합니다"),
  description: z
    .string()
    .min(10, "설명은 10자 이상이어야 합니다")
    .max(2000),
  startingPrice: z
    .number()
    .positive("시작 가격은 0보다 커야 합니다")
    .max(999_999_999, "최대 금액을 초과했습니다"),
  category: z.nativeEnum(ItemCategory),
  closingDate: z
    .string()
    .refine((val) => new Date(val) > new Date(), "마감일은 현재 이후여야 합니다"),
});

// Type derived from schema — always in sync
export type CreateAuctionForm = z.infer<typeof createAuctionSchema>;
```

---

#### `form-react-hook-form-pattern`
**Use React Hook Form with Zod resolver consistently**

✅ Good: Standard form pattern
```typescript
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

function CreateAuctionForm() {
  const form = useForm<CreateAuctionForm>({
    resolver: zodResolver(createAuctionSchema),
    defaultValues: {
      title: "",
      description: "",
      startingPrice: 0,
      category: "WEAPON",
    },
  });

  const mutation = useMutation({
    mutationFn: createAuction,
    onSuccess: () => {
      toast.success("경매가 생성되었습니다");
      navigate("/auctions");
    },
    onError: (error) => {
      const apiError = parseApiError(error);
      if (apiError.type === "/problems/validation-error") {
        applyServerErrors(apiError.errors, form.setError);
      }
    },
  });

  return (
    <form onSubmit={form.handleSubmit((data) => mutation.mutate(data))}>
      <FormField
        label="제목"
        error={form.formState.errors.title?.message}
      >
        <input {...form.register("title")} />
      </FormField>
      {/* ... */}
      <button type="submit" disabled={mutation.isPending}>
        {mutation.isPending ? "생성 중..." : "경매 생성"}
      </button>
    </form>
  );
}
```

---

#### `form-reusable-field-component`
**Create reusable form field wrapper for consistent error display**

✅ Good: Generic FormField component
```typescript
interface FormFieldProps {
  label: string;
  error?: string;
  required?: boolean;
  description?: string;
  children: ReactNode;
}

export function FormField({ label, error, required, description, children }: FormFieldProps) {
  return (
    <div className="space-y-1.5">
      <label className="text-sm font-medium">
        {label}
        {required && <span className="text-red-500 ml-0.5">*</span>}
      </label>
      {description && <p className="text-xs text-muted-foreground">{description}</p>}
      {children}
      {error && (
        <p className="text-xs text-red-500" role="alert">{error}</p>
      )}
    </div>
  );
}
```

---

### 5. Error Boundaries & Suspense (MEDIUM-HIGH)

#### `error-boundary-granular`
**Place error boundaries at feature level, not just app level**

❌ Bad: Single global error boundary
```typescript
<ErrorBoundary>
  <App />  {/* One error crashes everything */}
</ErrorBoundary>
```

✅ Good: Granular error boundaries per feature
```typescript
// Reusable error boundary with fallback
import { ErrorBoundary, FallbackProps } from "react-error-boundary";

function FeatureErrorFallback({ error, resetErrorBoundary }: FallbackProps) {
  return (
    <div className="rounded-lg border border-red-200 bg-red-50 p-4">
      <h3 className="font-medium text-red-800">문제가 발생했습니다</h3>
      <p className="text-sm text-red-600">{error.message}</p>
      <button onClick={resetErrorBoundary} className="mt-2 text-sm underline">
        다시 시도
      </button>
    </div>
  );
}

// Layout with isolated error boundaries
function AuctionPage() {
  return (
    <div className="grid grid-cols-3 gap-4">
      <div className="col-span-2">
        <ErrorBoundary FallbackComponent={FeatureErrorFallback}>
          <Suspense fallback={<AuctionDetailSkeleton />}>
            <AuctionDetail />
          </Suspense>
        </ErrorBoundary>
      </div>
      <aside>
        <ErrorBoundary FallbackComponent={FeatureErrorFallback}>
          <Suspense fallback={<BidHistorySkeleton />}>
            <BidHistory />
          </Suspense>
        </ErrorBoundary>
      </aside>
    </div>
  );
}
```

---

#### `error-suspense-skeleton`
**Use skeleton UI as Suspense fallback, not spinners**

❌ Bad: Generic spinner
```typescript
<Suspense fallback={<Spinner />}>
```

✅ Good: Content-shaped skeleton
```typescript
function AuctionCardSkeleton() {
  return (
    <div className="animate-pulse rounded-lg border p-4">
      <div className="h-40 rounded bg-gray-200" />
      <div className="mt-3 h-5 w-3/4 rounded bg-gray-200" />
      <div className="mt-2 h-4 w-1/2 rounded bg-gray-200" />
      <div className="mt-4 flex justify-between">
        <div className="h-6 w-20 rounded bg-gray-200" />
        <div className="h-6 w-16 rounded bg-gray-200" />
      </div>
    </div>
  );
}

function AuctionListSkeleton() {
  return (
    <div className="grid grid-cols-3 gap-4">
      {Array.from({ length: 6 }, (_, i) => (
        <AuctionCardSkeleton key={i} />
      ))}
    </div>
  );
}
```

---

#### `error-react-query-error-boundary`
**Integrate React Query with error boundaries**

✅ Good: React Query error boundary integration
```typescript
// Configure React Query to throw to error boundaries
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      throwOnError: (error) => {
        // Only throw 5xx errors to error boundary
        // 4xx errors handled in component
        if (axios.isAxiosError(error)) {
          return (error.response?.status ?? 500) >= 500;
        }
        return true;
      },
      retry: (failureCount, error) => {
        if (axios.isAxiosError(error) && error.response?.status === 404) {
          return false; // Don't retry 404s
        }
        return failureCount < 3;
      },
    },
  },
});
```

---

### 6. Project Structure (MEDIUM)

#### `dir-feature-based`
**Organize by feature, not by file type**

❌ Bad: Grouped by type
```
src/
├── components/    # 50+ files, mixed features
├── hooks/         # Where does useAuctionBid go?
├── types/         # All types in one folder
├── utils/         # Grab bag
└── pages/
```

✅ Good: Feature-based with shared lib
```
src/
├── app/                       # App-level setup
│   ├── App.tsx
│   ├── router.tsx
│   └── providers.tsx
├── features/                  # Feature modules
│   ├── auctions/
│   │   ├── components/
│   │   │   ├── AuctionCard.tsx
│   │   │   ├── AuctionDetail.tsx
│   │   │   └── AuctionGrid.tsx
│   │   ├── hooks/
│   │   │   ├── useAuctionList.ts
│   │   │   └── useAuctionBid.ts
│   │   ├── types.ts           # Feature-specific types
│   │   └── index.ts           # Public API (barrel export)
│   ├── auth/
│   │   ├── components/
│   │   ├── hooks/
│   │   └── index.ts
│   └── market/
│       ├── components/
│       ├── hooks/
│       └── index.ts
├── shared/                    # Truly shared code
│   ├── components/            # Design system components
│   │   ├── Button.tsx
│   │   ├── FormField.tsx
│   │   └── Skeleton.tsx
│   ├── hooks/                 # Generic hooks
│   │   ├── useDebounce.ts
│   │   └── useLocalStorage.ts
│   ├── lib/                   # Utility functions
│   │   ├── cn.ts
│   │   └── format.ts
│   └── types/                 # Shared types
│       └── common.ts
├── api/                       # API layer
│   ├── generated/             # OpenAPI generated types/clients
│   ├── config.ts
│   ├── custom-instance.ts
│   └── query-keys.ts
└── config/
    └── env.ts
```

---

#### `dir-barrel-exports`
**Use barrel exports (index.ts) to control public API per feature**

✅ Good: Feature exposes only what's needed
```typescript
// features/auctions/index.ts
export { AuctionCard } from "./components/AuctionCard";
export { AuctionDetail } from "./components/AuctionDetail";
export { AuctionGrid } from "./components/AuctionGrid";
export { useAuctionList } from "./hooks/useAuctionList";
export type { AuctionFilters } from "./types";

// Internal components NOT exported — implementation detail
// BidFormInternal, PriceCalculator, etc.
```

```typescript
// Consuming code
import { AuctionCard, useAuctionList } from "@/features/auctions";
```

---

#### `dir-path-aliases`
**Configure path aliases for clean imports**

✅ Good: TypeScript + Vite path aliases
```json
// tsconfig.json
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"],
      "@/features/*": ["src/features/*"],
      "@/shared/*": ["src/shared/*"],
      "@/api/*": ["src/api/*"]
    }
  }
}
```

```typescript
// vite.config.ts
import { resolve } from "path";

export default defineConfig({
  resolve: {
    alias: {
      "@": resolve(__dirname, "src"),
    },
  },
});
```

---

## Anti-Patterns Cheat Sheet

| Anti-Pattern | Rule | Fix |
|-------------|------|-----|
| `any` in props/handlers | `ts-strict-event-handlers` | Explicit TypeScript types |
| Boolean prop explosion | `ts-discriminated-union-props` | Discriminated unions |
| Non-exhaustive switch | `ts-exhaustive-switch` | `assertNever` pattern |
| Component does fetch + render | `comp-single-responsibility` | Container/presentational split |
| `export default` everywhere | `comp-named-exports` | Named exports |
| Server data in useState | `state-server-vs-client` | React Query for server state |
| Manual form validation | `form-zod-schema-first` | Zod schema + RHF resolver |
| Single global ErrorBoundary | `error-boundary-granular` | Feature-level boundaries |
| Spinner for loading states | `error-suspense-skeleton` | Skeleton UI |
| Type-based folder structure | `dir-feature-based` | Feature-based structure |
| Raw number IDs | `ts-branded-types` | Branded types |
