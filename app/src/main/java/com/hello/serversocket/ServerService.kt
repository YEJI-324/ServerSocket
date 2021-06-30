package com.hello.serversocket

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.SocketException

class ServerService : Service() {

    private var serverThread: Thread? = null

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("not implement")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serverThread = ServerThread()

        serverThread?.start()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "com.hello.serversocket"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, "Test", importance)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val notification = Notification.Builder(this, channelId)
                    .setContentTitle("테스트 서버")
                    .setContentText("체스트 서버 진행중")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .build()

            startForeground(1111, notification)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        serverThread?.interrupt()

        super.onDestroy()
    }

    class ServerThread : Thread() {
        companion object {
            private const val TAG = "ServerThread"
        }

        override fun run() {
            Log.d("serverStart", "서버 실행")

            val port = 10001

            val server = ServerSocket(port)

            try {
                while (true) {
                    val socket = server.accept()

                    val inputStream = ObjectInputStream(socket.getInputStream())
                    val input = inputStream.readObject()
                    Log.d("socketInput", "input stream : $input")

                    val outputStream = ObjectOutputStream(socket.getOutputStream())
                    outputStream.writeObject("200 OK from server -- client data: $input")
                    outputStream.flush()
                    Log.d("socketOutput", "클라이언트로 결과 보냄")

                    socket.close()
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            super.run()
        }
    }
}