package com.helpdesk.app.domain.model

data class TicketStats(
    val totalTickets: Int,
    val openTickets: Int,
    val aiResolvedTickets: Int,
    val aiResolvedPercentage: Double,
    val averageResolutionTime: String
)

data class DailyStat(
    val date: String,
    val count: Int
)
