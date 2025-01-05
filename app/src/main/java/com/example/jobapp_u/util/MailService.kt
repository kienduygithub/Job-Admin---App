package com.example.jobapp_u.util

import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.concurrent.thread

class MailService {

    private val mail: String = "buikienduy2020@gmail.com";
    private val password: String = "unxcjiwlcqvloiqx";

    fun sendEmail(subject: String, content: String, toEmail: String) {
        val host = "smtp.gmail.com"
        val properties = Properties().apply {
            put("mail.smtp.host", host)
            put("mail.smtp.port", "465")
            put("mail.smtp.ssl.enable", "true")
            put("mail.smtp.auth", "true")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(mail, password)
            }
        })

        try {
            val mimeMessage = MimeMessage(session).apply {
                addRecipient(Message.RecipientType.TO, InternetAddress(toEmail))
                setSubject(subject)
                setText(content)
            }

            // Send email in a separate thread
            thread {
                try {
                    Transport.send(mimeMessage)
                } catch (e: MessagingException) {
                    e.printStackTrace()
                }
            }
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }
}