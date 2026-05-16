package com.example.ksheerasagara.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate

object PdfExporter {

    fun generateAndShare(
        context: Context,
        month: String,
        totalIncome: Double,
        totalExpense: Double,
        netProfitLoss: Double,
        totalLiters: Double,
        profitPerLiter: Double,
        fodder: Double,
        medical: Double,
        labor: Double
    ) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.parseColor("#1B5E20")
            textSize = 28f
            isFakeBoldText = true
        }

        val headingPaint = Paint().apply {
            color = Color.parseColor("#2E7D32")
            textSize = 20f
            isFakeBoldText = true
        }

        val normalPaint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
        }

        val valuePaint = Paint().apply {
            color = Color.parseColor("#1565C0")
            textSize = 16f
            isFakeBoldText = true
        }

        val linePaint = Paint().apply {
            color = Color.parseColor("#BDBDBD")
            strokeWidth = 1f
        }

        val profitColor = if (netProfitLoss >= 0)
            Color.parseColor("#2E7D32") else Color.parseColor("#C62828")

        val profitPaint = Paint().apply {
            color = profitColor
            textSize = 22f
            isFakeBoldText = true
        }

        var y = 60f

        // Header background
        val bgPaint = Paint().apply { color = Color.parseColor("#E8F5E9") }
        canvas.drawRect(0f, 0f, 595f, 100f, bgPaint)

        // Title
        canvas.drawText("Ksheera-Sagara", 30f, 45f, titlePaint)
        canvas.drawText("Dairy Profit Report", 30f, 75f, headingPaint)
        canvas.drawText("Month: $month", 380f, 45f, normalPaint)
        canvas.drawText(
            "Generated: ${LocalDate.now()}",
            380f, 70f,
            Paint().apply { textSize = 12f; color = Color.GRAY }
        )

        y = 130f

        // Net P&L box
        val plBg = Paint().apply {
            color = if (netProfitLoss >= 0)
                Color.parseColor("#E8F5E9") else Color.parseColor("#FFEBEE")
        }
        canvas.drawRoundRect(30f, y, 565f, y + 80f, 10f, 10f, plBg)
        val plLabel = if (netProfitLoss >= 0) "PROFIT THIS MONTH" else "LOSS THIS MONTH"
        canvas.drawText(plLabel, 180f, y + 30f, profitPaint)
        canvas.drawText(
            "Rs ${"%.2f".format(netProfitLoss)}",
            210f, y + 62f,
            Paint().apply {
                color = profitColor; textSize = 26f; isFakeBoldText = true
            }
        )
        y += 100f

        // Divider
        canvas.drawLine(30f, y, 565f, y, linePaint)
        y += 20f

        // Income & Expense
        canvas.drawText("Financial Summary", 30f, y, headingPaint)
        y += 30f

        fun drawRow(label: String, value: String, yPos: Float) {
            canvas.drawText(label, 50f, yPos, normalPaint)
            canvas.drawText(value, 400f, yPos, valuePaint)
        }

        drawRow("Total Milk Income:", "Rs ${"%.2f".format(totalIncome)}", y); y += 30f
        drawRow("Total Expenses:", "Rs ${"%.2f".format(totalExpense)}", y); y += 30f
        drawRow("Total Liters Sold:", "${"%.1f".format(totalLiters)} L", y); y += 30f
        drawRow("Profit per Liter:", "Rs ${"%.2f".format(profitPerLiter)}", y); y += 30f

        // Divider
        canvas.drawLine(30f, y, 565f, y, linePaint)
        y += 20f

        // Expense Breakdown
        canvas.drawText("Expense Breakdown", 30f, y, headingPaint)
        y += 30f

        drawRow("Fodder Cost:", "Rs ${"%.2f".format(fodder)}", y); y += 30f
        drawRow("Medical Cost:", "Rs ${"%.2f".format(medical)}", y); y += 30f
        drawRow("Labor Cost:", "Rs ${"%.2f".format(labor)}", y); y += 30f

        val otherExpense = totalExpense - fodder - medical - labor
        drawRow("Other Costs:", "Rs ${"%.2f".format(otherExpense)}", y); y += 30f

        // Divider
        canvas.drawLine(30f, y, 565f, y, linePaint)
        y += 20f

        // Tips section
        canvas.drawText("Smart Tips", 30f, y, headingPaint)
        y += 25f

        val tipPaint = Paint().apply { color = Color.parseColor("#424242"); textSize = 14f }

        if (fodder > totalIncome * 0.6) {
            canvas.drawText("* Fodder cost is over 60% of income.", 40f, y, tipPaint); y += 22f
            canvas.drawText("  Consider growing napier grass locally.", 40f, y, tipPaint); y += 28f
        }
        if (netProfitLoss < 0) {
            canvas.drawText("* You are currently at a loss.", 40f, y, tipPaint); y += 22f
            canvas.drawText("  Review your highest expense category.", 40f, y, tipPaint); y += 28f
        }
        if (profitPerLiter < 5) {
            canvas.drawText("* Profit per liter is low (below Rs 5).", 40f, y, tipPaint); y += 22f
            canvas.drawText("  Try negotiating a better milk rate.", 40f, y, tipPaint); y += 28f
        }

        // Footer
        y = 800f
        canvas.drawLine(30f, y, 565f, y, linePaint)
        y += 20f
        canvas.drawText(
            "Ksheera-Sagara | Empowering Dairy Farmers",
            100f, y,
            Paint().apply { color = Color.GRAY; textSize = 12f }
        )

        document.finishPage(page)

        // Save to file
        val fileName = "KsheeraSagara_Report_$month.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)
        document.writeTo(FileOutputStream(file))
        document.close()

        // Share via intent
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Monthly Dairy Report - $month")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Report"))
    }
}