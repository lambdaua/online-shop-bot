package template_manager

import TemplateManager
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.litote.kmongo.id.jackson.IdJacksonModule

class ManagerTest {

//    companion object {
//        lateinit var manager: TemplateManager
//
//        @BeforeClass
//        @JvmStatic
//        fun managerInitializer() {
//            val mapper = ObjectMapper()
//            mapper.registerModule(KotlinModule())
//            mapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
//            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//            mapper.registerModule(IdJacksonModule())
//
//            manager = TemplateManager(mapper)
//        }
//    }
//
//    @Test
//    fun `Basic test for availability all elements from JSON`() {
//        val sections = manager.sections()
//
//        val coffeeSection = manager.section(id = 1)!!
//        val accessoriesSection = manager.section(id = 2)!!
//        val tShirtsSection = manager.section(id = 3)!!
//
//        //имена секций
//        val coffeeSectionByName = manager.sectionByName(name = "Кофе")
//        val accessoriesSectionByName = manager.sectionByName(name = "Аксессуары")
//        val tShirtsSectionByName = manager.sectionByName(name = "Футболки")
//
//        //PostStage размер
//        val coffeePostStages = coffeeSection.postStage()
//        val accessoriesPostStages = accessoriesSection.postStage()
//        val tShirtsPostStages = tShirtsSection.postStage()
//
//        //NextPostStage имена
//        val coffeeNextPostStage = coffeeSection.nextPostStageFor(id = 0)
//        val accessoriesNextPostStage = accessoriesSection.nextPostStageFor(id = 0)
//        val tShirtsNextPostStage = tShirtsSection.nextPostStageFor(id = 0)
//
//        //Product имена
//        val coffeeProduct = coffeeSection.product(id = 1)
//        val accessoriesProduct = accessoriesSection.product(id = 1)
//        val tShirtsProduct = tShirtsSection.product(id = 1)
//
//        //количество TotalProducts
//        val totalCoffeeProduct = coffeeSection.totalProducts()
//        val totalAccessoriesProduct = accessoriesSection.totalProducts()
//        val totalTShirtsProduct = tShirtsSection.totalProducts()
//
//        //количество Stage
//        val coffeeStages = coffeeSection.stage()
//        val accessoriesStages = accessoriesSection.stage()
//        val tShirtsStages = tShirtsSection.stage()
//
//        //имя NextStage
//        val nextCoffeeStages = coffeeSection.nextStage(id = 1)
//        val nextAccessoriesStages = accessoriesSection.nextStage(id = 1)
//        val nextTShirtsStages = tShirtsSection.nextStage(id = 1)
//
//
//        //количество секций
//        Assert.assertEquals(3, sections.size)
//
//        //имена секций
//        Assert.assertEquals("Кофе", coffeeSectionByName?.name)
//        Assert.assertEquals("Аксессуары", accessoriesSectionByName?.name)
//        Assert.assertEquals("Футболки", tShirtsSectionByName?.name)
//
//        //PostStage размер
//        Assert.assertEquals(3, coffeePostStages?.size)
//        Assert.assertEquals(1, accessoriesPostStages?.size)
//        Assert.assertEquals(1, tShirtsPostStages?.size)
//
//        //NextPostStage имена
//        Assert.assertEquals("Вид кофе", coffeeNextPostStage?.name)
//        Assert.assertEquals("Весы", accessoriesNextPostStage?.name)
//        Assert.assertEquals("Рисунок", tShirtsNextPostStage?.name)
//
//        //Product price
//        Assert.assertEquals("201", coffeeProduct?.price)
//        Assert.assertEquals("4125", accessoriesProduct?.price)
//        Assert.assertEquals("400", tShirtsProduct?.price)
//
//        //количество TotalProducts
//        Assert.assertEquals(2, totalCoffeeProduct)
//        Assert.assertEquals(1, totalAccessoriesProduct)
//        Assert.assertEquals(1, totalTShirtsProduct)
//
//        //количество Stage
//        Assert.assertEquals(2, coffeeStages?.size)
//        Assert.assertEquals(1, accessoriesStages?.size)
//        Assert.assertEquals(1, tShirtsStages?.size)
//
//        //имя NextStage
//        Assert.assertEquals("Обжарщик", nextCoffeeStages?.name)
//        Assert.assertEquals("Вид оборудования", nextAccessoriesStages?.name)
//        Assert.assertEquals("Рисунок", nextTShirtsStages?.name)
//    }
//
//    @Test
//    fun `SectionData with nonexistent id`() {
//        val sectionData = manager.section(id = 4)
//        Assert.assertEquals(null, sectionData)
//    }
//
//    @Test
//    fun `List SectionData first`() {
//        val listSectionData = manager.sections()
//        Assert.assertEquals("Кофе", listSectionData.first().name)
//    }
//
//    @Test
//    fun `SectionData List PostStages first`() {
//        val sectionData = manager.section(id = 1)
//        val listPostStages = sectionData?.postStages
//        Assert.assertEquals("Обжарка", listPostStages?.first()?.name)
//    }
//
//    @Test
//    fun `SectionData with nonexistent name`() {
//        val sectionData = manager.sectionByName("Уммаёт")
//        Assert.assertEquals(null, sectionData)
//    }
//
//    @Test
//    fun `NextPostStageFor with nonexistent id`() {
//        val sectionData = manager.section(id = 1)
//        val nextPostStageFor = sectionData?.nextPostStageFor(id = 1)
//
//        Assert.assertEquals(null, nextPostStageFor)
//    }
//
//    @Test
//    fun `Product with nonexistent id`() {
//        val sectionData = manager.section(id = 2)
//        val product = sectionData?.product(id = 4)
//
//        Assert.assertEquals(null, product)
//    }
//
//    @Test
//    fun `Products size with nonexistent SectionData`() {
//        val sectionData = manager.section(id = 4)
//        val productSize = sectionData?.totalProducts()
//
//        Assert.assertEquals(null, productSize)
//    }
//
//    @Test
//    fun `SectionData List Stages first`() {
//        val sectionData = manager.section(id = 3)
//        val listStages = sectionData?.stage()
//
//        Assert.assertEquals("Рисунок", listStages?.first()?.name)
//    }
}