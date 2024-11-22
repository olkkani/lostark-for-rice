package io.oikkani.lfr.util

import com.github.f4b6a3.tsid.TsidCreator


fun createTsid(): Long {
    return TsidCreator.getTsid().toLong()


}