/**
 * URL normalization utility: ensures base URLs have the correct scheme and trailing slash.
 */
package com.helpdesk.app.core.util

/**
 * Normalizes a raw URL string into a well-formed base URL.
 *
 * - Prepends `http://` for local addresses (localhost, 127.0.0.1, 10.0.2.2, 192.168.*)
 *   and `https://` for all other hosts.
 * - Ensures the URL ends with a trailing slash.
 */
object BaseUrlNormalizer {

    fun normalize(raw: String): String {
        val trimmed = raw.trim()
        val withScheme = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            if (isLocalAddress(trimmed)) {
                "http://$trimmed"
            } else {
                "https://$trimmed"
            }
        } else {
            trimmed
        }
        return if (withScheme.endsWith("/")) withScheme else "$withScheme/"
    }

    private fun isLocalAddress(host: String): Boolean =
        host.startsWith("localhost") ||
            host.startsWith("10.0.2.2") ||
            host.startsWith("127.0.0.1") ||
            host.startsWith("192.168.")
}
