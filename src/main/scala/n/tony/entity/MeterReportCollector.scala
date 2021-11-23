
package n.tony.entity

import com.akkaserverless.scalasdk.valueentity.ValueEntity
import com.akkaserverless.scalasdk.valueentity.ValueEntityContext
import n.tony.api
import n.tony.api.{CollectedReportDto, MeterTakeDto, StatusDto}
import n.tony.entity.MeterReportCollector.HISTORY_LENGTH
import org.slf4j.LoggerFactory

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

/** A value entity. */
class MeterReportCollector(context: ValueEntityContext) extends AbstractMeterReportCollector {

  val entityId = context.entityId
  val log = LoggerFactory.getLogger(getClass().getSimpleName())

  override def emptyState: MeterReport =
    MeterReport(entityId, takes = Seq.empty)

  override def report(currentState: MeterReport, meterReportDto: api.MeterReportDto): ValueEntity.Effect[api.StatusDto] = {
    log.info("Report from " + meterReportDto.meterId + " with timestamp [" + meterReportDto.timestamp + "]")
    effects.updateState(MeterReport(meterId = meterReportDto.meterId,
      meterReportDto.timestamp,
      currentState.takes.concat(meterReportDto.takes.map(fromDto))
        .takeRight(HISTORY_LENGTH)
    ))
      .thenReply(new StatusDto(200))
  }

  override def getReport(currentState: MeterReport, getCollectedReportDto: api.GetCollectedReportDto): ValueEntity.Effect[api.CollectedReportDto] = {
    log.debug("Incoming report request for meter [" + currentState.meterId + "]")
    effects.reply(CollectedReportDto(currentState.meterId, currentState.takes.map(toDto)))
  }

  def toDto: MeterTake => MeterTakeDto = take =>
    new MeterTakeDto(take.unixTime, take.value, take.orderNumber)

  def fromDto: MeterTakeDto => MeterTake = take =>
    new MeterTake(take.unixTime, take.value, take.orderNumber)
}

object MeterReportCollector {
  val HISTORY_LENGTH = 10
}
