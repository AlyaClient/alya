render = {
    drawRect = function(x, y, width, height, color)
        alya.render.drawRect(x, y, width, height, color)
    end,
    drawRectOutline = function(x, y, width, height, color, borderWidth)
        alya.render.drawRectOutline(x, y, width, height, color, borderWidth)
    end,
    drawRoundedRect = function(x, y, width, height, radius, color)
        alya.render.drawRoundedRect(x, y, width, height, radius, color)
    end,
    drawGradientRect = function(x, y, width, height, colorLeft, colorRight)
        alya.render.drawGradientRect(x, y, width, height, colorLeft, colorRight)
    end,
    drawVerticalGradientRect = function(x, y, width, height, colorTop, colorBottom)
        alya.render.drawVerticalGradientRect(x, y, width, height, colorTop, colorBottom)
    end,
    drawLine = function(x1, y1, x2, y2, lineWidth, color)
        alya.render.drawLine(x1, y1, x2, y2, lineWidth, color)
    end,
    drawCircle = function(x, y, radius, color)
        alya.render.drawCircle(x, y, radius, color)
    end,
}
