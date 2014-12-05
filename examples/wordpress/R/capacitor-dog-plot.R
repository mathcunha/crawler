require(ggplot2)

best_effort <- data.frame(c("BF","BF","BF","BF","BF","BF","BF","BF","BF","BF"),c(10000,20000,30000,40000,50000,60000,70000,80000,90000,100000),c(280,280,280,280,280,280,280,280,280,280),c(178.5,178.5,178.5,178.5,178.5,178.5,178.5,178.5,178.5,178.5))
colnames(best_effort) <- c("heuristic","sla","exec","cost")

capacity = read.csv("1414438523-capacity.csv", header = TRUE, stringsAsFactors = FALSE)
capacity <- rbind(capacity, best_effort)
capacity$sla <- factor(capacity$sla, order=TRUE)
capacity$heuristic <- factor(capacity$heuristic, order=TRUE)

str(capacity)

cost = read.csv("1414438523-cost.csv", header = TRUE, stringsAsFactors = FALSE)
cost <- rbind(cost, best_effort)
cost$sla <- factor(cost$sla, order=TRUE)
cost$heuristic <- factor(cost$heuristic, order=TRUE)

#pdf("Execution_x_Prediction.pdf", width = 9.5, height = 6)

graph_base <- capacity

graph_base <- subset(graph_base, heuristic != "CR")
graph_base <- subset(graph_base, heuristic != "OR")
graph_base <- subset(graph_base, heuristic != "PR")
graph_base <- subset(graph_base, heuristic != "RR")
graph_base <- subset(graph_base, heuristic != "RC")
graph_base <- subset(graph_base, heuristic != "RO")
graph_base <- subset(graph_base, heuristic != "RP")
graph_base <- subset(graph_base, heuristic != "RR50")
graph_base <- subset(graph_base, heuristic != "RR10")
graph_base <- subset(graph_base, sla <= 50000)

png(filename="ExecutionCount-Capacity.png", width = 9.5, height = 6, res = 480, units = "in")
ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = exec, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  geom_line(aes(group=heuristic, colour=heuristic, y = exec), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  scale_x_discrete("SLA") + 
  scale_y_continuous("Execução") + 
  labs(colour = "Heurística", shape="Heurística") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )
dev.off()

png(filename="ExecutionCost-Capacity.png", width = 9.5, height = 6, res = 480, units = "in")
ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = cost, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  geom_line(aes(group=heuristic, colour=heuristic, y = cost), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  scale_x_discrete("SLA") + 
  scale_y_continuous("U$/Hora") + 
  labs(colour = "Heurística", shape="Heurística") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )
dev.off()
graph_base <- subset(graph_base, heuristic != "BF")

ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = exec/280, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  geom_line(aes(group=heuristic, colour=heuristic, y = exec/280), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  scale_x_discrete("SLA") + 
  scale_y_continuous("Execução") + 
  labs(colour = "Heurística", shape="Heurística") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = cost/178.5, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  geom_line(aes(group=heuristic, colour=heuristic, y = cost/178.5), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  scale_x_discrete("SLA") + 
  scale_y_continuous("U$/Hora") + 
  labs(colour = "Heurística", shape="Heurística") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )


graph_base <- cost

graph_base <- subset(graph_base, heuristic != "CR")
graph_base <- subset(graph_base, heuristic != "OR")
graph_base <- subset(graph_base, heuristic != "PR")
graph_base <- subset(graph_base, heuristic != "RR")
graph_base <- subset(graph_base, heuristic != "RC")
graph_base <- subset(graph_base, heuristic != "RO")
graph_base <- subset(graph_base, heuristic != "RP")
graph_base <- subset(graph_base, heuristic != "RR50")
graph_base <- subset(graph_base, heuristic != "RR10")
graph_base <- subset(graph_base, sla <= 50000)

png(filename="ExecutionCount-Price.png", width = 9.5, height = 6, res = 480, units = "in")
ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = exec, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  geom_line(aes(group=heuristic, colour=heuristic, y = exec), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  scale_x_discrete("SLA") + 
  scale_y_continuous("Execução") + 
  labs(colour = "Heurística", shape="Heurística") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )
dev.off()

png(filename="ExecutionCost-Price.png", width = 9.5, height = 6, res = 480, units = "in")
ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = cost, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  geom_line(aes(group=heuristic, colour=heuristic, y = cost), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  scale_x_discrete("SLA") + 
  scale_y_continuous("U$/Hora") + 
  labs(colour = "Heurística", shape="Heurística") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )
dev.off()
graph_base <- subset(graph_base, heuristic != "BF")

ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = exec/280, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  geom_line(aes(group=heuristic, colour=heuristic, y = exec/280), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  scale_x_discrete("SLA") + 
  scale_y_continuous("Execução") + 
  labs(colour = "Heurística", shape="Heurística") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = cost/178.5, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  geom_line(aes(group=heuristic, colour=heuristic, y = cost/178.5), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  scale_x_discrete("SLA") + 
  scale_y_continuous("U$/Hora") + 
  labs(colour = "Heurística", shape="Heurística") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

#dev.off()
