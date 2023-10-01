package com.peter.enermizer.utils

class Common {

    companion object {
        fun buildIpaddress(ipaddress: String): String {
            return "http://$ipaddress/api/"
        }
    }
}