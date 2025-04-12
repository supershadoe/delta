package dev.shadoe.delta.data.softap

import javax.inject.Inject

class SoftApStateFacadeClosable
@Inject
constructor(val facade: SoftApStateFacade) : AutoCloseable {
  init {
    facade.start()
  }

  override fun close() {
    facade.stop()
  }
}
