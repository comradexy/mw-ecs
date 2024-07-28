# Scheduled Task Manager

> [@Scheduled，Quartz，XXL-JOB三种定时任务总结](https://blog.csdn.net/m0_72075879/article/details/134794515)

Schedule是计划执行任务的通用术语。Quartz是Java任务调度框架，支持灵活任务管理。XXL-JOB是分布式任务调度平台，注重大规模系统，提供分布式任务调度和管理，包括动态调度、监控、日志记录等功能。选择取决于应用需求，Quartz适用于Java应用，XXL-JOB适用于分布式环境。

- `@Scheduled` 简单易用，但不支持持久化；
- `quartz` 支持任务持久化，但需要复杂的配置；
- `xxl-job` 功能丰富，适用于分布式场景，但是对于简单场景来说，又显得过于庞大冗余。

本项目基于`org.springframework.scheduling` 包实现一个简单易用的定时任务管理中心，便于动态创建、停止定时任务。作为扩展功能，支持任务持久化，实现任务的暂定和恢复功能，添加新任务时计算其与已有任务的相似度，提供相似任务提醒。



## TODO

- 完善 `pauseTask` 和 `resumeTask` 方法，需要先完成任务持久化的实现（[参考Quartz的任务持久化原理](#Quartz任务持久化)），持久化任务恢复后正确执行应当考虑：
  - 幂等性：要求任务逻辑本身具有幂等性，即多次执行同一任务应该产生相同的结果。这有助于避免在故障恢复后由于重复执行任务而导致的问题。

  - 任务状态管理：允许任务在执行过程中更新其状态。通过将状态信息存储在数据库中，跟踪任务的执行进度，并在必要时进行恢复。

  - 触发器恢复策略：支持多种触发器恢复策略，如“立即恢复”和“下次触发时恢复”。这些策略允许在故障恢复后根据具体情况选择如何重新调度任务。


- 相似任务提醒，任务相似度计算方案设计
- 目前使用的是 `ThreadPoolTaskScheduler` （和 `ConcurrentTaskScheduler` 有什么区别？），`ThreadPoolTaskScheduler` 使用 `ScheduledTreadPoolExecutor` 作为底层实现，而 `ScheduledTreadPoolExecutor` 使用的**无界的延迟阻塞队列 `DelayedWorkQueue` **，任务队列**最大长度为 `Integer.MAX_VALUE`** ，<u>（如果一直创建定时任务）可能堆积大量的请求，从而导致 OOM</u>，**需要为 `ScheduledTaskMgr` 设计最大任务数和拒绝策略**，以免发生OOM。
- 配置 `StringValueResolver` ，解析字符串中的占位符和 SpEL 表达式。
- 如果要考虑分布式并发场景（防止任务重复执行），可以使用分布式锁（例如Redisson），配合任务唯一标识符进行分布式管理。



## Note

- `@Scheduled` 默认以**单线程模式**执行（如果没有配置 `TaskScheduler` ，Spring会给 `ScheduledTaskRegister#TaskScheduler` 配置一个底层实现为 `SingleThreadScheduledExecutor` 的 `ConcurrentTaskScheduler` ），若需要并发执行定时任务，可以通过 `@Async` 和 `@EnableAsync` 注解实现（方法上加 `@Async` ，启动类上添加 `@EnableAsync` 注解）。
- `@PostConstrut` 在Bean实例化后就会立即执行，参考[spring探秘:通过BeanPostProcessor、@PostConstruct、InitializingBean在启动前执行方法](https://www.cnblogs.com/feng-gamer/p/12001205.html)

 

## Reference

<span id="Scheduled的实现原理"></span>

- [通过源码理解Spring中@Scheduled的实现原理并且实现调度任务动态装载](https://www.cnblogs.com/throwable/p/12616945.html)

<span id="Quartz任务持久化"></span>

- [Quartz如何处理任务的持久化？](https://blog.csdn.net/u012680662/article/details/136927337#:~:text=Quartz%E9%80%9A%E8%BF%87%E9%9B%86%E6%88%90%E6%95%B0%E6%8D%AE%E5%BA%93%E6%94%AF%E6%8C%81%E6%9D%A5%E5%AE%9E%E7%8E%B0%E4%BB%BB%E5%8A%A1%E7%9A%84%E6%8C%81%E4%B9%85%E5%8C%96%E3%80%82,%E5%85%B7%E4%BD%93%E6%9D%A5%E8%AF%B4%EF%BC%8CQuartz%E4%BD%BF%E7%94%A8JobStore%E6%9D%A5%E5%AD%98%E5%82%A8%E5%92%8C%E7%AE%A1%E7%90%86%E4%BB%BB%E5%8A%A1%E7%9A%84%E7%9B%B8%E5%85%B3%E4%BF%A1%E6%81%AF%EF%BC%8C%E5%8C%85%E6%8B%AC%E4%BB%BB%E5%8A%A1%E7%9A%84%E5%AE%9A%E4%B9%89%E3%80%81%E7%8A%B6%E6%80%81%E3%80%81%E8%A7%A6%E5%8F%91%E5%99%A8%E7%9A%84%E8%AE%BE%E7%BD%AE%E7%AD%89%E3%80%82%20%E9%80%9A%E8%BF%87%E5%B0%86%E8%BF%99%E4%BA%9B%E4%BF%A1%E6%81%AF%E5%AD%98%E5%82%A8%E5%9C%A8%E6%95%B0%E6%8D%AE%E5%BA%93%E4%B8%AD%EF%BC%8CQuartz%E8%83%BD%E5%A4%9F%E5%9C%A8%E7%B3%BB%E7%BB%9F%E9%87%8D%E5%90%AF%E6%88%96%E6%95%85%E9%9A%9C%E6%81%A2%E5%A4%8D%E5%90%8E%E9%87%8D%E6%96%B0%E5%8A%A0%E8%BD%BD%E4%BB%BB%E5%8A%A1%EF%BC%8C%E5%B9%B6%E7%A1%AE%E4%BF%9D%E4%BB%BB%E5%8A%A1%E8%83%BD%E5%A4%9F%E6%AD%A3%E7%A1%AE%E6%89%A7%E8%A1%8C%E3%80%82)

<span id="理解@Order注解"></span>

- [深入理解Spring的@Order注解和Ordered接口](https://blog.csdn.net/zkc7441976/article/details/112548075)

