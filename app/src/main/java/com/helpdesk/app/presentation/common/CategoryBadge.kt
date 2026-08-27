package com.helpdesk.app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helpdesk.app.core.theme.CatGeneralBgDark
import com.helpdesk.app.core.theme.CatGeneralBgLight
import com.helpdesk.app.core.theme.CatGeneralTextDark
import com.helpdesk.app.core.theme.CatGeneralTextLight
import com.helpdesk.app.core.theme.CatRefundBgDark
import com.helpdesk.app.core.theme.CatRefundBgLight
import com.helpdesk.app.core.theme.CatRefundTextDark
import com.helpdesk.app.core.theme.CatRefundTextLight
import com.helpdesk.app.core.theme.CatTechBgDark
import com.helpdesk.app.core.theme.CatTechBgLight
import com.helpdesk.app.core.theme.CatTechTextDark
import com.helpdesk.app.core.theme.CatTechTextLight
import com.helpdesk.app.domain.model.TicketCategory

@Composable
fun CategoryBadge(
    category: TicketCategory,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val (icon, bgColor, textColor) = when (category) {
        TicketCategory.GENERAL_QUESTION -> Triple(
            Icons.AutoMirrored.Outlined.HelpOutline,
            if (isDark) CatGeneralBgDark else CatGeneralBgLight,
            if (isDark) CatGeneralTextDark else CatGeneralTextLight
        )
        TicketCategory.TECHNICAL_QUESTION -> Triple(
            Icons.Outlined.Laptop,
            if (isDark) CatTechBgDark else CatTechBgLight,
            if (isDark) CatTechTextDark else CatTechTextLight
        )
        TicketCategory.REFUND_REQUEST -> Triple(
            Icons.Outlined.CurrencyExchange,
            if (isDark) CatRefundBgDark else CatRefundBgLight,
            if (isDark) CatRefundTextDark else CatRefundTextLight
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = if (isDark) 0.35f else 0.2f), RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 3.5.dp)
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
                    fontSize = 11.5.sp,
                    letterSpacing = 0.2.sp
                )
            )
        }
    }
}

