package nl.inholland.group9.karmakebab.data.networks

import nl.inholland.group9.karmakebab.data.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class TokenInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Fetch the token from TokenManager
//        val token = runBlocking { tokenManager.token.first() }
        val token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ3WjJuZW9NOERTSUhwZzVZNk5aM3pQdmZ1RTg3N3luZHVKMTBia3JXQTZzIn0.eyJleHAiOjE3MzMxNTcyNzEsImlhdCI6MTczMzE1Njk3MSwianRpIjoiMDUwZWJjODMtNjZjMC00OWQ1LWE3MzEtZjZlYjRiNGExODU0IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9rYXJtYS1rZWJhYi1yZWFsbSIsInN1YiI6ImJmMGU3YTg4LTAzMDUtNGU4MC05MzU3LWQ0YjdlZDQ0ZjRkOCIsInR5cCI6IkJlYXJlciIsImF6cCI6Imthcm1hLWtlYmFiLWNsaWVudCIsInNpZCI6ImY3ZDI2OTA0LTVkOTQtNDZiNC05MTUxLTIxZTkyOTY3ZjRhYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDozMDA1IiwiaHR0cDovL2xvY2FsaG9zdDozMDA0IiwiaHR0cDovL2xvY2FsaG9zdDozMDAzIiwiaHR0cDovL2xvY2FsaG9zdDozMDAyIiwiaHR0cDovL2xvY2FsaG9zdDozMDA2IiwiaHR0cDovL2xvY2FsaG9zdDozMDAxIl0sInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IlRlc3QgVXNlciIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3R1c2VyIiwiZ2l2ZW5fbmFtZSI6IlRlc3QiLCJmYW1pbHlfbmFtZSI6IlVzZXIiLCJlbWFpbCI6InRlc3R1c2VyQGV4YW1wbGUuY29tIn0.PjQOkeCD3Wi5odsdjN9v4HjaNedW8FprNcTx-HnQz4JR_aJw1Yeny45Bv5ht88AhiMqOndKd3qwD9x6p1nu1DGN-5LWnnmM0KY11msMrO3pfJdYXebKUgFV1Bl31FjY-UeX9uYtok-KMiLrdrOpBz2gnX2esrtlikB9JxHmJPJvnB7_P6lfCi1i9oFt9M8m9dZZlLIU7FojkIJoSsWcPACP0TG0OtxxDgwflOx4qKUtIt6_y3Ve950rYIRSIotMMoHduKaqMt3EWvNxbBaTtJJezGLsp4TacU1v4PcaCS6_78rfpFk2Ff8lZl7gbkxWb-byrvcC4J7-EtXlPuCILcA"

        // Add the token to the request if it exists
        val newRequest = if (!token.isNullOrEmpty()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}