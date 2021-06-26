# CloudSimSFC
服务调度： 包括NFV调度和Microservice调度两个研究点。

工作记录：[wiki](http://gitlab.act.buaa.edu.cn/service-scheduling/service-scheduling/-/wikis/home)
## 代码阅读
### link
SmallSFCDemo/sfc-example-physical.json 和  SmallSFCDemo/sfc-example-virtual.json 中存在两种link,前种类是Link， 后种类是 FlowConfig(表示VM间的traffic)
### Workload
SmallSFCDemo/sfc-example-physical.json
{generateTime,workloadSubmitVm,submitPackageSize,{ReuqestCloudletLen,LinkName,toVmName,TransimissionSize},{ReuqestCloudletLen,LinkName,toVmName,TransimissionSize}....}