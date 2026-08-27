package com.helpdesk.app.presentation.tickets.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helpdesk.app.core.theme.AiCardBgDark
import com.helpdesk.app.core.theme.AiCardBgLight
import com.helpdesk.app.core.theme.AiCardBorderDark
import com.helpdesk.app.core.theme.AiCardBorderLight
import com.helpdesk.app.core.theme.AiCardTextDark
import com.helpdesk.app.core.theme.AiCardTextLight
import com.helpdesk.app.core.theme.AiGradientEnd
import com.helpdesk.app.core.theme.AiGradientStart
import com.helpdesk.app.core.theme.Primary
import com.helpdesk.app.core.util.DateTimeUtils
import com.helpdesk.app.domain.model.Reply
import com.helpdesk.app.domain.model.ReplySenderType

@Composable
fun ReplyBubble(reply: Reply) {
    val isDark = isSystemInDarkTheme()
    val isAgent = reply.senderType == ReplySenderType.AGENT

    val bubbleBg = if (isAgent) {
        if (isDark) Primary.copy(alpha = 0.16f) else Color(0xFFF1F5F9)
    } else {
        if (isDark) MaterialTheme.colorScheme.surface else Color.White
    }

    val bubbleBorder = if (isAgent) {
        if (isDark) Primary.copy(alpha = 0.35f) else Primary.copy(alpha = 0.25f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.3f else 0.5f)
    }

    val bubbleShape = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomStart = if (isAgent) 14.dp else 2.dp,
        bottomEnd = if (isAgent) 2.dp else 14.dp
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isAgent) Arrangement.End else Arrangement.Start
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(bubbleShape)
                    .border(1.dp, bubbleBorder, bubbleShape),
                color = bubbleBg,
                tonalElevation = if (isDark) 2.dp else 0.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isAgent) Primary else Color(0xFF64748B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (reply.author.name ?: "U").take(1).uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = reply.author.name ?: reply.author.email,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (isAgent) {
                                            if (isDark) Color(0xFF1E1B4B) else Color(0xFFEEF2FF)
                                        } else {
                                            if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                                        }
                                    )
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isAgent) "Agent" else "Customer",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = if (isAgent) {
                                        if (isDark) Color(0xFFA5B4FC) else Primary
                                    } else {
                                        if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                                    }
                                )
                            }
                        }

                        Text(
                            text = DateTimeUtils.formatRelativeTime(reply.createdAt),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = reply.content,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 20.sp,
                            fontSize = 13.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ReplyComposer(
    draftReply: String,
    onDraftChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onPolishClick: () -> Unit,
    isSending: Boolean,
    isPolishing: Boolean,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        tonalElevation = if (isDark) 3.dp else 0.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            OutlinedTextField(
                value = draftReply,
                onValueChange = onDraftChange,
                placeholder = {
                    Text(
                        "Type response to customer...",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp)
                    )
                },
                minLines = 3,
                maxLines = 6,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AI Polish Action
                OutlinedButton(
                    onClick = onPolishClick,
                    enabled = !isPolishing && draftReply.isNotBlank(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isDark) Color(0xFFC084FC) else Color(0xFF8B5CF6)
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    if (isPolishing) {
                        CircularProgressIndicator(
                            color = if (isDark) Color(0xFFC084FC) else Color(0xFF8B5CF6),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Polishing...", style = MaterialTheme.typography.labelSmall)
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("✨ AI Polish", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
                    }
                }

                // Send Button
                Button(
                    onClick = onSendClick,
                    enabled = !isSending && draftReply.isNotBlank(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.height(40.dp)
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Reply", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
                    }
                }
            }
        }
    }
}

@Composable
fun PolishPreviewDialog(
    originalText: String,
    polishedText: String,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFFC084FC) else Color(0xFF8B5CF6),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "AI Polished Reply",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "AI has rewritten your draft for professional tone, empathy, and clarity:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            1.dp,
                            if (isDark) AiCardBorderDark else AiCardBorderLight,
                            RoundedCornerShape(10.dp)
                        ),
                    color = if (isDark) AiCardBgDark else AiCardBgLight
                ) {
                    Text(
                        text = polishedText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 20.sp,
                            fontSize = 13.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Use Polished Text")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Keep Original")
            }
        }
    )
}

