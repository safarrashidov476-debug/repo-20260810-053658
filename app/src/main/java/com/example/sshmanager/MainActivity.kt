package com.example.sshmanager

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sshmanager.databinding.ActivityMainBinding
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var session: Session? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConnect.setOnClickListener {
            connectToServer()
        }

        binding.btnExecute.setOnClickListener {
            executeCommand()
        }

        binding.btnDisconnect.setOnClickListener {
            disconnect()
        }
    }

    private fun connectToServer() {
        val host = binding.etHost.text.toString().trim()
        val port = binding.etPort.text.toString().toIntOrNull() ?: 22
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (host.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Barcha maydonlarni to‘ldiring!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnConnect.isEnabled = false
        binding.tvOutput.text = "Ulanmoqda...\n"

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val jsch = JSch()
                    session = jsch.getSession(username, host, port).apply {
                        setPassword(password)
                        setConfig("StrictHostKeyChecking", "no")
                        connect(15000)
                    }
                }
                binding.tvOutput.append("✓ Serverga muvaffaqiyatli ulandi!\nHost: $host:$port\n")
                binding.btnExecute.isEnabled = true
                binding.btnDisconnect.isEnabled = true
                binding.btnConnect.isEnabled = false
                Toast.makeText(this@MainActivity, "Ulandi!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                binding.tvOutput.append("Xato: ${e.message}\n")
                Toast.makeText(this@MainActivity, "Ulanishda xato: ${e.message}", Toast.LENGTH_LONG).show()
                binding.btnConnect.isEnabled = true
            }
        }
    }

    private fun executeCommand() {
        val command = binding.etCommand.text.toString().trim()
        if (command.isEmpty()) {
            Toast.makeText(this, "Buyruq yozing!", Toast.LENGTH_SHORT).show()
            return
        }

        if (session == null || !session!!.isConnected) {
            Toast.makeText(this, "Avval serverga ulaning!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnExecute.isEnabled = false
        binding.tvOutput.append("\n> $command\n")

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val channel = session!!.openChannel("exec") as ChannelExec
                    val outputStream = ByteArrayOutputStream()
                    val errorStream = ByteArrayOutputStream()

                    channel.setCommand(command)
                    channel.outputStream = outputStream
                    channel.setErrStream(errorStream)
                    channel.connect(10000)

                    while (!channel.isClosed) {
                        Thread.sleep(50)
                    }

                    val exitStatus = channel.exitStatus
                    channel.disconnect()

                    val output = outputStream.toString("UTF-8")
                    val error = errorStream.toString("UTF-8")

                    buildString {
                        if (output.isNotEmpty()) append(output)
                        if (error.isNotEmpty()) append("\n[stderr]: $error")
                        if (exitStatus != 0) append("\n[exit code]: $exitStatus")
                        if (isEmpty()) append("(bo‘sh javob)")
                    }
                }
                binding.tvOutput.append("$result\n")
                // Scroll to bottom
                binding.scrollView.post {
                    binding.scrollView.fullScroll(android.view.View.FOCUS_DOWN)
                }
            } catch (e: Exception) {
                binding.tvOutput.append("Xato: ${e.message}\n")
            } finally {
                binding.btnExecute.isEnabled = true
            }
        }
    }

    private fun disconnect() {
        session?.disconnect()
        session = null
        binding.btnConnect.isEnabled = true
        binding.btnExecute.isEnabled = false
        binding.btnDisconnect.isEnabled = false
        binding.tvOutput.append("\n✗ Ulanish uzildi.\n")
        Toast.makeText(this, "Ulanish uzildi", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        session?.disconnect()
    }
}
