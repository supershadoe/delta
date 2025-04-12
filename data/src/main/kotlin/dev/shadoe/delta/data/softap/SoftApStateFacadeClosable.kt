package dev.shadoe.delta.data.softap

class SoftApStateFacadeClosable(val facade: SoftApStateFacade) : AutoCloseable {
  init {
    facade.start()
  }

  override fun close() {
    facade.stop()
  }
}
