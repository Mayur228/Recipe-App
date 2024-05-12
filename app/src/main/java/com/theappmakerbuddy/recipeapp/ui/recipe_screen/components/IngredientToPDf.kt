package com.theappmakerbuddy.recipeapp.ui.recipe_screen.components

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.text.*
import android.util.Log
import androidx.core.graphics.withTranslation
import com.theappmakerbuddy.recipeapp.data.remote.dto.recipes.Ingredient

fun ingredientToPDF(
    ingredient: List<Ingredient>,
    name: String
): PdfDocument {
    val pdfDocument = PdfDocument()
    val pageWidth = 720
    val pageHeight = 1190
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1)
        .create()

    val page = pdfDocument.startPage(pageInfo)
    if (ingredient.size < 22) {
        page.canvas.apply {
            getCanvasContent(canvas = this, ingredient = ingredient, name = name)
        }
        pdfDocument.finishPage(page)
    }else {
        page.canvas.apply {
            getCanvasContent(canvas = this, ingredient = ingredient.subList(0, 22), name = name)
        }
        pdfDocument
            .finishPage(page)
        val page2 =
            pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create())
        page2.canvas.apply {
            getCanvasContent(canvas = this, ingredient = ingredient.subList(22, ingredient.lastIndex), name = name)
        }
        pdfDocument.finishPage(page2)
    }

    return pdfDocument
}

fun getCanvasContent(canvas: Canvas, ingredient: List<Ingredient>, name: String): Canvas {
    return canvas.apply {
        val width = this.width
        drawRect(Rect(0, 0, this.width, this.height), Paint().apply {
            color = Color.YELLOW
        })

        drawText("Get your recipe", 10f, 52f, Paint().apply {
            this.textSize = 40f
            this.color = Color.BLACK
        })

        drawText(name, 210f, 52f, Paint().apply {
            this.textSize = 40f
            this.color = Color.BLACK
        })

        drawLine(0f, 60f, width.toFloat(), 60f, Paint().apply {
            strokeWidth = 5f
            this.color = Color.BLACK
        })
        val x = 5f
        var y = 65f
        for (i in ingredient) {
            drawMultilineText(
                text = i.toString(),
                textPaint = TextPaint().apply {
                    this.color = Color.BLACK
                    this.textSize = 30f
                },
                width = width - 10,
                x = x,
                y = y,
                start = 0
            )
            y += 50
            if (y > this.height) {

            }
        }
    }
}

fun Canvas.drawMultilineText(
    text: CharSequence,
    textPaint: TextPaint,
    width: Int,
    x: Float,
    y: Float,
    start: Int = 0,
    end: Int = text.length,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    textDir: TextDirectionHeuristic = TextDirectionHeuristics.LTR,
    spacingMult: Float = 1f,
    spacingAdd: Float = 0f,
) {

    val staticLayout = StaticLayout.Builder
        .obtain(text, start, end, textPaint, width)
        .setAlignment(alignment)
        .setTextDirection(textDir)
        .setLineSpacing(spacingAdd, spacingMult)
        .build()

    withTranslation(x, y) {
        staticLayout.draw(this)
    }
}