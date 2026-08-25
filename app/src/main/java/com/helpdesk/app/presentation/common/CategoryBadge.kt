package com.helpdesk.app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helpdesk.app.domain.model.TicketCategory

@Composable
fun CategoryBadge(
    category: TicketCategory,
    modifier: Modifier = Modifier
) {
    val (icon, bgColor, textColor) = when (category) {
        TicketCategory.GENERAL_QUESTION -> Triple(
            Icons.Outlined.HelpOutline,
            Color(0xFFF1F5F9),
            Color(0xFF475569)
        )
        TicketCategory.TECHNICAL_QUESTION -> Triple(
            Icons.Outlined.Laptop,
            Color(0xFFEDE9FE), // Purple 100
            Color(0xFF7C3AED)  // Purple 600
        )
        TicketCategory.REFUND_REQUEST -> Triple(
            Icons.Outlined.CurrencyExchange,
            Color(0xFFFFEDD5), // Orange 100
            Color(0xFFEA580C)  // Orange 600
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = category.label,
                color = textColor,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                )
            )
        }
    }
}
