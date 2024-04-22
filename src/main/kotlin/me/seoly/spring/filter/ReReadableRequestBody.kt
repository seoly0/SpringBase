package me.seoly.spring.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper

import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

import org.apache.commons.lang3.StringUtils
import org.zeroturnaround.zip.commons.IOUtils

class ReReadableRequestBody: Filter {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {

        chain?.doFilter(RequestWrapper(request as HttpServletRequest), response)
    }

    class RequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

        private val encoding: Charset
        private var rawData: ByteArray

        init {
            var characterEncoding = request.characterEncoding
            if (StringUtils.isBlank(characterEncoding)) {
                characterEncoding = StandardCharsets.UTF_8.name()
            }
            this.encoding = Charset.forName(characterEncoding)

            try {
                val inputStream: InputStream = request.inputStream
                this.rawData = IOUtils.toByteArray(inputStream)
            } catch (e: IOException) {
                throw e
            }
        }

        override fun getInputStream(): ServletInputStream {
            val byteArrayInputStream = ByteArrayInputStream(this.rawData)
            return object : ServletInputStream() {
                override fun isFinished(): Boolean {
                    return false
                }

                override fun isReady(): Boolean {
                    return false
                }

                override fun setReadListener(readListener: ReadListener) {
                }

                @Throws(IOException::class)
                override fun read(): Int {
                    return byteArrayInputStream.read()
                }
            }
        }

        override fun getReader(): BufferedReader {
            return BufferedReader(InputStreamReader(this.inputStream, this.encoding))
        }

        override fun getRequest(): ServletRequest {
            return super.getRequest()
        }
    }
}

