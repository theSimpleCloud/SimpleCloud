package eu.thesimplecloud.module.serviceselection.api

import eu.thesimplecloud.api.service.ICloudService
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * Date: 29.06.2020
 * Time: 13:38
 * @author Frederick Baier
 */
class ServiceSelectionTest {

    private lateinit var serviceViewer: AbstractServiceViewer

    @Before
    fun setUp() {
        this.serviceViewer = createServiceDisplayer()
    }

    @Test
    fun newServiceDisplayer_ServiceNull() {
        assertNull(this.serviceViewer.service)
    }

    @Test
    fun setService_ServiceEqualsSetService() {
        val service = createDummyService()
        this.serviceViewer.service = service
        assertEquals(service, this.serviceViewer.service)
    }

    private fun createServiceDisplayer(): AbstractServiceViewer {
        return spyk<AbstractServiceViewer>()
    }

    private fun createDummyService(): ICloudService {
        return mockk<ICloudService>()
    }

}