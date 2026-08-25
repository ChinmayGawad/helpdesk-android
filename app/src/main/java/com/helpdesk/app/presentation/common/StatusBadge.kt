package com.helpdesk.app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helpdesk.app.core.theme.StatusClosedBg
import com.helpdesk.app.core.theme.StatusClosedText
import com.helpdesk.app.core.theme.StatusNewBg
import com.helpdesk.app.core.theme.StatusNewText
import com.helpdesk.app.core.theme.StatusOpenBg
import com.helpdesk.app.core.theme.StatusOpenText
import com.helpdesk.app.core.theme.StatusProcessingBg
import com.helpdesk.app.core.theme.StatusProcessingText
import com.helpdesk.app.core.theme.StatusResolvedBg
import com.helpdesk.app.core.theme.StatusResolvedText
import com.helpdesk.app.domain.model.TicketStatus

@Composable
fun StatusBadge(
    status: TicketStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        TicketStatus.NEW -> StatusNewBg to StatusNewText
        TicketStatus.PROCESSING -> StatusProcessingBg to StatusProcessingText
        TicketStatus.OPEN -> StatusOpenBg to StatusOpenText
        TicketStatus.RESOLVED -> StatusResolvedBg to StatusResolvedText
        TicketStatus.CLOSED -> StatusClosedBg to StatusClosedText
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status.label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        )
    }
}
