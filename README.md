# CloudSimSFC

**CloudSimSFC** is a toolkit for simulating SFC in MDSN environments. 

**Service Function Chain** Service function chain (SFC) is widely adopted in multi-domain service networks (MDSN) to enforce network policies on Customer traffic. Great effort has been devoted to the research of SFC deployment strategies. In this context, simulators are helpful to the design and evaluation of those strategies. A suitable simulator should be expressive toward what might influence the performance of the SFC. However, existing SFC simulators fall short of this need due to two major drawbacks: First, they overlook the resource heterogeneity and performance instability of the MDSN environment. Second, their performance models for service functions are too general to contain distinctive features. 


In contrast, CloudSimSFC has the following features
- It considers the performance fluctuations and server CompVirtualTopologyVmSFs in MDSN environment to align with the performance instability of real-world systems
- It elaborates the modeling of service functions to involve the computation components such as CPU and queue, and the traffic changing effect which happens during packet processing.
- It employs scenario abstraction to simplify the definition of new (heterogeneous) simulation scenarios and supports standard service metrics like latency and price. 
These features endow CloudSimSFC with the quality to evaluate the SFC deployment strategies regarding resource consumption and service efficiency.



**Usage**
CloudSimSFC provides an SFC model with straight-forward notations, so that researchers (of SFC) could easily understand the whole Simulation process.

To start a simulation process, serveral configurations should be ready. 
- The resource specification for the physical environment should be configured as a MDSN resource abstraction.
- The service function demands


The characteristics of service functions are included in 'ServiceFunctionDescription.json' as an example, which should includes:
- service function name 'Name'
- service function type 'Type'
- computation complexity 'OperationalComplexity'
- traffic changing ratio declared with 'InputRate' and 'OutputRate'
