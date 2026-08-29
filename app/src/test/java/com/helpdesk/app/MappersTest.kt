package com.helpdesk.app

import com.helpdesk.app.data.mapper.toDomain
import com.helpdesk.app.data.remote.dto.CustomerDto
import com.helpdesk.app.data.remote.dto.DailyStatDto
import com.helpdesk.app.data.remote.dto.ReplyDto
import com.helpdesk.app.data.remote.dto.StatsResponse
import com.helpdesk.app.data.remote.dto.TicketDto
import com.helpdesk.app.data.remote.dto.UserDto
import com.helpdesk.app.domain.model.ReplySenderType
import com.helpdesk.app.domain.model.TicketCategory
import com.helpdesk.app.domain.model.TicketSource
import com.helpdesk.app.domain.model.TicketStatus
import com.helpdesk.app.domain.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MappersTest {

    @Test
    fun userDtoToDomain_mapsFieldsCorrectly() {
        val dto = UserDto(
            id = "u1",
            name = "Admin User",
            email = "admin@helpdesk.local",
            role = "admin",
            createdAt = "2026-08-29T10:00:00Z"
        )
        val domain = dto.toDomain()
        assertEquals("u1", domain.id)
        assertEquals("Admin User", domain.name)
        assertEquals("admin@helpdesk.local", domain.email)
        assertEquals(UserRole.ADMIN, domain.role)
    }

    @Test
    fun customerDtoToDomain_mapsFieldsCorrectly() {
        val dto = CustomerDto(id = "c1", name = "Jane", email = "jane@example.com")
        val domain = dto.toDomain()
        assertEquals("c1", domain.id)
        assertEquals("Jane", domain.name)
        assertEquals("jane@example.com", domain.email)
    }

    @Test
    fun ticketDtoToDomain_mapsFieldsAndEnumsCorrectly() {
        val dto = TicketDto(
            id = "t1",
            subject = "Issue with login",
            description = "Cannot login",
            status = "open",
            category = "technical_question",
            source = "email",
            createdAt = "2026-08-29T10:00:00Z",
            requester = CustomerDto(id = "c1", name = "Jane", email = "jane@example.com"),
            assignee = UserDto(id = "u1", name = "Admin", email = "admin@helpdesk.local", role = "admin")
        )
        val domain = dto.toDomain()
        assertEquals("t1", domain.id)
        assertEquals(TicketStatus.OPEN, domain.status)
        assertEquals(TicketCategory.TECHNICAL_QUESTION, domain.category)
        assertEquals(TicketSource.EMAIL, domain.source)
        assertNotNull(domain.assignee)
        assertEquals(UserRole.ADMIN, domain.assignee?.role)
    }

    @Test
    fun replyDtoToDomain_mapsFieldsCorrectly() {
        val dto = ReplyDto(
            id = "r1",
            content = "We have fixed your issue",
            ticketId = "t1",
            authorId = "u1",
            senderType = "agent",
            author = UserDto(id = "u1", name = "Admin", email = "admin@helpdesk.local", role = "admin"),
            createdAt = "2026-08-29T11:00:00Z"
        )
        val domain = dto.toDomain()
        assertEquals("r1", domain.id)
        assertEquals("We have fixed your issue", domain.content)
        assertEquals(ReplySenderType.AGENT, domain.senderType)
    }

    @Test
    fun statsDtoToDomain_mapsFieldsCorrectly() {
        val dto = StatsResponse(
            totalTickets = 10,
            openTickets = 4,
            aiResolvedTickets = 3,
            aiResolvedPercentage = 30.0,
            averageResolutionTime = "1h 30m"
        )
        val domain = dto.toDomain()
        assertEquals(10, domain.totalTickets)
        assertEquals(4, domain.openTickets)
        assertEquals(3, domain.aiResolvedTickets)
        assertEquals(30.0, domain.aiResolvedPercentage, 0.01)
        assertEquals("1h 30m", domain.averageResolutionTime)
    }
}
