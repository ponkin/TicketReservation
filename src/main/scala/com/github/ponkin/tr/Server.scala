package com.github.ponkin.tr

import akka.util.Timeout

/**
 * Created by aponkin on 26.04.2017.
 */
case class Server(host: String, port: Int, timeout: Timeout)
