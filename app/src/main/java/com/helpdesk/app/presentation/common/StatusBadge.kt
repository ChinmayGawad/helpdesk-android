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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.helpdesk.app.core.theme.StatusClosedBgDark
import com.helpdesk.app.core.theme.StatusClosedBgLight
import com.helpdesk.app.core.theme.StatusClosedTextDark
import com.helpdesk.app.core.theme.StatusClosedTextLight
import com.helpdesk.app.core.theme.StatusNewBgDark
import com.helpdesk.app.core.theme.StatusNewBgLight
import com.helpdesk.app.core.theme.StatusNewTextDark
import com.helpdesk.app.core.theme.StatusNewTextLight
import com.helpdesk.app.core.theme.StatusOpenBgDark
import com.helpdesk.app.core.theme.StatusOpenBgLight
import com.helpdesk.app.core.theme.StatusOpenTextDark
import com.helpdesk.app.core.theme.StatusOpenTextLight
import com.helpdesk.app.core.theme.StatusProcessingBgDark
import com.helpdesk.app.core.theme.StatusProcessingBgLight
import com.helpdesk.app.core.theme.StatusProcessingTextDark
import com.helpdesk.app.core.theme.StatusProcessingTextLight
import com.helpdesk.app.core.theme.StatusResolvedBgDark
import com.helpdesk.app.core.theme.StatusResolvedBgLight
import com.helpdesk.app.core.theme.StatusResolvedTextDark
import com.helpdesk.app.core.theme.StatusResolvedTextLight
import com.helpdesk.app.domain.model.TicketStatus

@Composable
fun StatusBadge(
    status: TicketStatus,
    modifier: Modifier = Modifier,
    showDot: Boolean = true
) {
    val isDark = isSystemInDarkTheme()

    val (bgColor, textColor, dotColor) = when (status) {
        TicketStatus.NEW -> Triple(
            if (isDark) StatusNewBgDark else StatusNewBgLight,
            if (isDark) StatusNewTextDark else StatusNewTextLight,
            Color(0xFF64748B)
        )
        TicketStatus.PROCESSING -> Triple(
            if (isDark) StatusProcessingBgDark else StatusProcessingBgLight,
            if (isDark) StatusProcessingTextDark else StatusProcessingTextLight,
            Color(0xFFF59E0B)
        )
        TicketStatus.OPEN -> Triple(
            if (isDark) StatusOpenBgDark else StatusOpenBgLight,
            if (isDark) StatusOpenTextDark else StatusOpenTextLight,
            Color(0xFF3B82F6)
        )
        TicketStatus.RESOLVED -> Triple(
            if (isDark) StatusResolvedBgDark else StatusResolvedBgLight,
            if (isDark) StatusResolvedTextDark else StatusResolvedTextLight,
            Color(0xFF22C55E)
        )
        TicketStatus.CLOSED -> Triple(
            if (isDark) StatusClosedBgDark else StatusClosedBgLight,
            if (isDark) StatusClosedTextDark else StatusClosedTextLight,
            Color(0xFF94A3B8)
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = if (isDark) 0.35f else 0.2f), RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 3.5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showDot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = status.label,
                color = textColor,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp,
                    letterSpacing = 0.2.sp
                )
            )
        }
    }
}

