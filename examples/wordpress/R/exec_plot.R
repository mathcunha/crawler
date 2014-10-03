setwd("C:/Users/Matheus/VMS/crawler/repos/crawler/examples/wordpress/R")
require(ggplot2)
require(doBy)
roc = read.csv("cap_result.csv", header = TRUE, stringsAsFactors = FALSE)
roc$workload <- factor(roc$workload, order=TRUE)

pdf("Execution_x_Prediction.pdf", width = 9.5, height = 6)


capacitor <- roc
graph_base <- summaryBy(capacitor$EXEC ~ capacitor$heuristic + capacitor$sla  , capacitor, FUN = c(sum))
graph_base_2 <- summaryBy(capacitor$PREDICT ~ capacitor$heuristic + +capacitor$sla  , capacitor, FUN = c(sum))
graph_base_3 <- summaryBy(capacitor$PRICE ~ capacitor$heuristic + +capacitor$sla  , capacitor, FUN = c(sum))
graph_base <- merge(graph_base, graph_base_2, by = c("heuristic","sla"))
graph_base <- merge(graph_base, graph_base_3, by = c("heuristic","sla"))
colnames(graph_base) <- c("heuristic","sla", "EXEC", "PREDICT","PRICE")
#U$ 178.5 - 280
BF <- data.frame(c("BF","BF","BF","BF","BF","BF","BF","BF","BF","BF"), c(10000,20000,30000,40000,50000,60000,70000,80000,90000,100000),c(280,280,280,280,280,280,280,280,280,280))
colnames(BF) <- c("heuristic","sla", "EXEC")
graph_base$sla <- factor(graph_base$sla, order=TRUE)


ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = EXEC, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  geom_line(aes(group=heuristic, colour=heuristic, y = EXEC), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  scale_x_discrete("Sla") + 
  scale_y_continuous("Execution") + 
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = PRICE, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  geom_line(aes(group=heuristic, colour=heuristic, y = PRICE), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  scale_x_discrete("Sla") + 
  scale_y_continuous("U$/Hour") + 
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )


ggplot(graph_base, aes(y = PRICE/178.5, x = EXEC/280)) +  
  geom_point(size=3, aes(colour=heuristic, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  facet_grid(sla ~ .) +  
  scale_y_continuous("Relative Price", limits=c(0, 1)) +
  scale_x_continuous("Relative Execution", limits=c(0, 1)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    axis.title.x  = element_text(face="bold"),
    axis.title.y  = element_text(face="bold")
  )

graph_base <- subset(graph_base, heuristic != "CR")
graph_base <- subset(graph_base, heuristic != "OR")
graph_base <- subset(graph_base, heuristic != "PR")
graph_base <- subset(graph_base, heuristic != "RR")
graph_base <- subset(graph_base, heuristic != "RC")
graph_base <- subset(graph_base, heuristic != "RO")
graph_base <- subset(graph_base, heuristic != "RP")


ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = EXEC, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  geom_line(aes(group=heuristic, colour=heuristic, y = EXEC), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  scale_x_discrete("Sla") + 
  scale_y_continuous("Execution") + 
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = PRICE, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  geom_line(aes(group=heuristic, colour=heuristic, y = PRICE), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  scale_x_discrete("Sla") + 
  scale_y_continuous("U$/Hour") + 
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )


ggplot(graph_base, aes(y = PRICE/178.5, x = EXEC/280)) +  
  geom_point(size=3, aes(colour=heuristic, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))+
  facet_grid(sla ~ .) +  
  scale_y_continuous("Relative Price", limits=c(0, 1)) +
  scale_x_continuous("Relative Execution", limits=c(0, 1)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    axis.title.x  = element_text(face="bold"),
    axis.title.y  = element_text(face="bold")
  )

dev.off()