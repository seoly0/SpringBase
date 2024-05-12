package me.seoly.spring.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

import java.io.*

class ReReadableRequestBody: Filter {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {

        chain?.doFilter(RequestWrapper(request as HttpServletRequest), response)
    }

    class RequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

        private val encoding: Charset
        private var rawData: ByteArray

        init {
            var characterEncoding = request.characterEncoding
            if (characterEncoding.isBlank()) {
                characterEncoding = StandardCharsets.UTF_8.name()
            }
            this.encoding = Charset.forName(characterEncoding)

            try {
                val inputStream: InputStream = request.inputStream
                this.rawData = inputStreamToByteArray(inputStream)
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

        private fun inputStreamToByteArray(inputStream: InputStream): ByteArray {
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val outputStream = ByteArrayOutputStream()

            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            return outputStream.toByteArray()
        }
    }
}

