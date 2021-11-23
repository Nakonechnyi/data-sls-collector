package n.tony.entity

import n.tony.api
import n.tony.api.GetCollectedReportDto
import n.tony.api.MeterTakeDto
import n.tony.api.StatusDto
import n.tony.entity.MeterReportCollector
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MeterReportCollectorSpec
    extends AnyWordSpec
    with Matchers {

  "MeterReportCollector" must {

    "handle command Report" in {
      val testKit = MeterReportCollectorTestKit(new MeterReportCollector(_))
      val take1 = MeterTakeDto.of(0, 1, 0)
      val take2 = MeterTakeDto.of(2, 2, 1)
      val report1 = testKit.report(api.MeterReportDto("meter_1", "1", Seq(take1, take2) ))
      report1.reply shouldBe StatusDto(200)

      val take3 = MeterTakeDto.of(4, 1, 3)
      val take4 = MeterTakeDto.of(6, 3, 4)
      val take5 = MeterTakeDto.of(8, 3, 5)
      val report2 = testKit.report(api.MeterReportDto("meter_1", "3",
        Seq(take3, take4, take5)
      ))
      report2.reply shouldBe StatusDto(200)

      val getReport1 = testKit.getReport(new GetCollectedReportDto("meter_1"))
      getReport1.reply.meterId shouldBe "meter_1"
      getReport1.reply.takes(0) shouldEqual take1
      getReport1.reply.takes(1) shouldEqual take2
      getReport1.reply.takes(2) shouldEqual take3
    }

    "handle last reports by History Length" in {
      val testKit = MeterReportCollectorTestKit(new MeterReportCollector(_))
      val meterTakes = List.tabulate(MeterReportCollector.HISTORY_LENGTH)(i => MeterTakeDto.of(i, i + 1, i))
      val report = api.MeterReportDto("meter_1", "3", meterTakes)
      testKit.report(report)

      val lastTake = MeterTakeDto.of(8, 3, 5)

      val report2 = testKit.report(api.MeterReportDto("meter_1", "3",
        Seq(lastTake)
      ))
      report2.reply shouldBe StatusDto(200)

      val getReport1 = testKit.getReport(new GetCollectedReportDto("meter_1"))
      getReport1.reply.meterId shouldBe "meter_1"
      println(getReport1.reply.takes.length)
      getReport1.reply.takes.length shouldBe MeterReportCollector.HISTORY_LENGTH
      getReport1.reply.takes(MeterReportCollector.HISTORY_LENGTH - 1) shouldEqual lastTake
    }

  }
}
