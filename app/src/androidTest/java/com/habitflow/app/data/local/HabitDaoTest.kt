package com.habitflow.app.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.habitflow.app.domain.FrequencyType
import com.habitflow.app.domain.HabitType
import com.habitflow.app.domain.SyncStatus
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: HabitDao

    private fun habit(id: String, userId: String = "user-1", archived: Boolean = false) = HabitEntity(
        id = id,
        userId = userId,
        name = "Navika $id",
        category = "Opšte",
        type = HabitType.BUILD,
        frequencyType = FrequencyType.DAILY,
        isArchived = archived,
        createdAt = 0L,
        updatedAt = 0L
    )

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.habitDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun upsertIGetById_vracaSacuvanuNaviku() = runBlocking {
        dao.upsert(habit("h1"))

        val result = dao.getById("h1")

        assertEquals("h1", result?.id)
    }

    @Test
    fun getActive_iskljucujeArhivirane() = runBlocking {
        dao.upsert(habit("h1", archived = false))
        dao.upsert(habit("h2", archived = true))

        val active = dao.getActive("user-1")

        assertEquals(1, active.size)
        assertEquals("h1", active.first().id)
    }

    @Test
    fun getActive_vracaSamoNavikeTogKorisnika() = runBlocking {
        dao.upsert(habit("h1", userId = "user-1"))
        dao.upsert(habit("h2", userId = "user-2"))

        val active = dao.getActive("user-1")

        assertEquals(1, active.size)
        assertEquals("h1", active.first().id)
    }

    @Test
    fun reassignOwner_prebacujeNavikeNaNovogKorisnika() = runBlocking {
        dao.upsert(habit("h1", userId = "local-user"))

        dao.reassignOwner("local-user", "server-user-1")

        val reassigned = dao.getById("h1")
        assertEquals("server-user-1", reassigned?.userId)
        assertEquals(SyncStatus.PENDING, reassigned?.syncStatus)
    }

    @Test
    fun getById_vracaNullZaNepostojecuNaviku() = runBlocking {
        assertNull(dao.getById("nema-je"))
    }

    @Test
    fun markSynced_menjaStatusSamoZaNavedeneIdjeve() = runBlocking {
        dao.upsert(habit("h1"))
        dao.upsert(habit("h2"))

        dao.markSynced(listOf("h1"))

        assertEquals(SyncStatus.SYNCED, dao.getById("h1")?.syncStatus)
        assertTrue(dao.getPending().map { it.id }.contains("h2"))
    }
}
