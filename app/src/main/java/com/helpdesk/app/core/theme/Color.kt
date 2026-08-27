package com.helpdesk.app.core.theme

import androidx.compose.ui.graphics.Color

// Primary Brand Colors (matching modern indigo/slate design)
val Primary = Color(0xFF4F46E5) // Indigo 600
val PrimaryLight = Color(0xFF6366F1) // Indigo 500
val PrimaryDark = Color(0xFF3730A3) // Indigo 800
val PrimaryContainer = Color(0xFFEEF2FF) // Indigo 50
val OnPrimaryContainer = Color(0xFF312E81)

val Secondary = Color(0xFF0EA5E9) // Sky 500
val SecondaryContainer = Color(0xFFE0F2FE)
val OnSecondaryContainer = Color(0xFF0369A1)

// Neutral & Backgrounds
val BackgroundLight = Color(0xFFF8FAFC) // Slate 50
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF1F5F9) // Slate 100
val BorderLight = Color(0xFFE2E8F0) // Slate 200
val TextPrimaryLight = Color(0xFF0F172A) // Slate 900
val TextSecondaryLight = Color(0xFF64748B) // Slate 500
val TextMutedLight = Color(0xFF94A3B8) // Slate 400

val BackgroundDark = Color(0xFF0F172A) // Slate 900
val SurfaceDark = Color(0xFF1E293B) // Slate 800
val SurfaceVariantDark = Color(0xFF334155) // Slate 700
val BorderDark = Color(0xFF334155)
val TextPrimaryDark = Color(0xFFF8FAFC) // Slate 50
val TextSecondaryDark = Color(0xFF94A3B8) // Slate 400
val TextMutedDark = Color(0xFF64748B) // Slate 500

// Semantic / Status Colors - Light
val StatusNewBgLight = Color(0xFFF1F5F9)
val StatusNewTextLight = Color(0xFF475569)
val StatusProcessingBgLight = Color(0xFFFEF3C7) // Amber 100
val StatusProcessingTextLight = Color(0xFFB45309) // Amber 700
val StatusOpenBgLight = Color(0xFFDBEAFE) // Blue 100
val StatusOpenTextLight = Color(0xFF1D4ED8) // Blue 700
val StatusResolvedBgLight = Color(0xFFDCFCE7) // Green 100
val StatusResolvedTextLight = Color(0xFF15803D) // Green 700
val StatusClosedBgLight = Color(0xFFF1F5F9) // Slate 100
val StatusClosedTextLight = Color(0xFF64748B) // Slate 500

// Semantic / Status Colors - Dark
val StatusNewBgDark = Color(0xFF1E293B)
val StatusNewTextDark = Color(0xFF94A3B8)
val StatusProcessingBgDark = Color(0xFF451A03) // Amber 950
val StatusProcessingTextDark = Color(0xFFFCD34D) // Amber 300
val StatusOpenBgDark = Color(0xFF172554) // Blue 950
val StatusOpenTextDark = Color(0xFF93C5FD) // Blue 300
val StatusResolvedBgDark = Color(0xFF052E16) // Green 950
val StatusResolvedTextDark = Color(0xFF86EFAC) // Green 300
val StatusClosedBgDark = Color(0xFF1E293B) // Slate 800
val StatusClosedTextDark = Color(0xFF94A3B8) // Slate 400

// Legacy compatibility aliases
val StatusNewBg = StatusNewBgLight
val StatusNewText = StatusNewTextLight
val StatusProcessingBg = StatusProcessingBgLight
val StatusProcessingText = StatusProcessingTextLight
val StatusOpenBg = StatusOpenBgLight
val StatusOpenText = StatusOpenTextLight
val StatusResolvedBg = StatusResolvedBgLight
val StatusResolvedText = StatusResolvedTextLight
val StatusClosedBg = StatusClosedBgLight
val StatusClosedText = StatusClosedTextLight

// Category Colors - Light
val CatGeneralBgLight = Color(0xFFF1F5F9)
val CatGeneralTextLight = Color(0xFF475569)
val CatTechBgLight = Color(0xFFEDE9FE) // Violet 100
val CatTechTextLight = Color(0xFF6D28D9) // Violet 700
val CatRefundBgLight = Color(0xFFFFEDD5) // Orange 100
val CatRefundTextLight = Color(0xFFC2410C) // Orange 700

// Category Colors - Dark
val CatGeneralBgDark = Color(0xFF1E293B)
val CatGeneralTextDark = Color(0xFF94A3B8)
val CatTechBgDark = Color(0xFF2E1065) // Violet 950
val CatTechTextDark = Color(0xFFC4B5FD) // Violet 300
val CatRefundBgDark = Color(0xFF431407) // Orange 950
val CatRefundTextDark = Color(0xFFFDBA74) // Orange 300

// AI Accent Colors
val AiGradientStart = Color(0xFF7C3AED) // Violet 600
val AiGradientEnd = Color(0xFFEC4899) // Pink 500
val AiCardBgLight = Color(0xFFFAF5FF) // Violet 50
val AiCardBorderLight = Color(0xFFE9D5FF) // Violet 200
val AiCardTextLight = Color(0xFF6B21A8) // Violet 800

val AiCardBgDark = Color(0xFF1E1B4B) // Indigo 950
val AiCardBorderDark = Color(0xFF4338CA) // Indigo 700
val AiCardTextDark = Color(0xFFC7D2FE) // Indigo 200

// Legacy aliases
val AiCardBg = AiCardBgLight
val AiCardBorder = AiCardBorderLight
val AiCardText = AiCardTextLight

// Error & Danger
val ErrorRed = Color(0xFFEF4444)
val ErrorContainerLight = Color(0xFFFEE2E2)
val OnErrorContainerLight = Color(0xFF991B1B)
val ErrorContainerDark = Color(0xFF450A0A)
val OnErrorContainerDark = Color(0xFFFCA5A5)

val ErrorContainer = ErrorContainerLight
val OnErrorContainer = OnErrorContainerLight
