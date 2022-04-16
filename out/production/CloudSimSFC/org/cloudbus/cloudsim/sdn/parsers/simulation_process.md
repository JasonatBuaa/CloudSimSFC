Figure ? shows a complete simulation process.

The user should prepare a \textit{resource description file} and a \textit{SFC demand file} before simulation. The former file contains the resource configuration of the MDSN environment, while the latter involves the customer SFC demands which will be used during simulation. The basic information of these two files are listed in table ?? and examples could be find in our github code base.

When the user starts the simulation, CloudSimSFC will looks for the aforementioned files. 

- ingress:
  - atime, ingress_name, packet_to_Ingress(0), workload (0)
- sf:
  - ,link_to_me, name, packet_to_me, my_workload
- egress:
- ,link_to_egress, egress_name, packet, workload (0)