setwd("C:/Users/Matheus/VMS/crawler/repos/crawler/examples/wordpress/R")
require(ggplot2)
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

graph_base$EXEC_SCALE[!is.na(graph_base$EXEC)] <- graph_base$EXEC /280
colnames(graph_base) <- c("heuristic","sla", "EXEC", "PREDICT", "SCALED")
graph_base$sla <- factor(graph_base$sla, order=TRUE)


ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = EXEC, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15))+
  geom_line(aes(group=heuristic, colour=heuristic, y = EXEC), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

ggplot(graph_base, aes(x = sla)) +
  geom_point(size=3, aes(colour=heuristic, y = SCALED, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15))+
  geom_line(aes(group=heuristic, colour=heuristic, y = SCALED), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

ggplot(graph_base, aes(x = sla)) +
  geom_jitter(size=3, aes(colour=heuristic, y = EXEC, shape=heuristic), fill="white") +  
  scale_shape_manual(values=c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15))+
  geom_line(aes(group=heuristic, colour=heuristic, y = EXEC), linetype="solid", size=1) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )
dev.off()

ggplot(graph_base, aes(x = sla)) +
  geom_point(size=4, aes(colour=heuristic, y = EXEC), shape=21, fill="white") +  
  geom_line(aes(group=heuristic, colour=heuristic, y = EXEC), linetype="dashed") +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

ggplot(graph_base, aes(x = EXEC)) +
  geom_point(size=4, aes(colour=heuristic, y = sla), shape=20) +    
  
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

ggplot(graph_base, aes(x = sla)) +
  geom_point(size=4, aes(colour=sla, y = EXEC), shape=21, fill="white") +  
  geom_line(aes(group=heuristic, y = EXEC), linetype="dashed") +
  facet_grid(. ~ heuristic ) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

ggplot(graph_base, aes(x = sla)) +
  geom_point(size=4, aes(colour=heuristic, y = EXEC), shape=21, fill="white") +  
  geom_line(aes(group=heuristic, colour=heuristic, y = EXEC), linetype="dashed") +
  facet_grid(heuristic ~ .) +
  
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

ggplot(graph_base, aes(x = sla)) +
  geom_point(size=5, aes(colour=heuristic, y = EXEC), shape=21, fill="white") +  
  geom_line(aes(group=heuristic, colour=heuristic, y = EXEC), linetype="dashed") +
  geom_text (aes(y = EXEC, label = EXEC, angle = 0), size = 3.5) +
  facet_grid(heuristic ~ .) +
  
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

ggplot(graph_base, aes(x = sla)) +  
  geom_bar(stat="identity",aes(y=EXEC, fill=sla)) +
  facet_grid(. ~ heuristic) +
  
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )


ggplot(graph_base, aes(x = sla)) +  
  geom_bar(stat="identity",aes(y=EXEC, fill=sla)) +
  geom_text (aes(y = 100, label = EXEC, angle = 0), size = 3.5) +
  facet_grid(heuristic ~ .) +
  
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

graph_base_all = data.frame(graph_base$heuristic, graph_base$sla, graph_base$PREDICT)
graph_base_all$type <- "Prediction"
graph_base_all$hjust <- 60
colnames(graph_base_all) <- c("heuristic", "sla", "exec", "type","hjust")
graph_base_2 = data.frame(graph_base$heuristic, graph_base$sla, graph_base$EXEC)
graph_base_2$type <- "Execution"
graph_base_2$hjust <- 250
colnames(graph_base_2) <- c("heuristic", "sla", "exec", "type","hjust")
graph_base_all <- rbind(graph_base_all, graph_base_2)

ggplot(graph_base_all, aes(x = sla)) +  
  geom_bar(stat="identity", position="stack",aes(y=exec, fill=type)) +
  geom_text (aes(y = hjust, label = exec, angle = 0), size = 2.5) +
  facet_grid(heuristic ~ .) +
  
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

dev.off()





ggplot(capacitor, aes(y = heuristic)) +
  geom_point(size=4, aes(colour=heuristic, x = PREDICT), shape=21, fill="white") +
  geom_point(size=4, aes(colour=heuristic, x = EXEC)) +
  geom_text (aes(x = PREDICT, label = PREDICT, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  geom_text (aes(x = EXEC, label = EXEC, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  facet_grid(. ~ workload) +
  scale_x_discrete("Preditcion",breaks=c(0,14,21,28)) +
  scale_y_discrete("heuristic") +
  ggtitle(paste("SLA <= ",iSla)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

iSla <- 20000
capacitor <- subset(roc, sla == iSla)
graph_base = data.frame(capacitor$heuristic, capacitor$workload, capacitor$PREDICT)
graph_base$type <- "Prediction"
colnames(graph_base) <- c("heuristic", "workload", "exec", "type")
graph_base_2 = data.frame(capacitor$heuristic, capacitor$workload, capacitor$EXEC)
graph_base_2$type <- "Execution"
colnames(graph_base_2) <- c("heuristic", "workload", "exec", "type")
graph_base <- rbind(graph_base, graph_base_2)

ggplot(graph_base, aes(x = heuristic)) +
  facet_grid(. ~ workload)+
  geom_bar(stat="identity", position="stack", aes(y=exec, fill=type)) +
  coord_flip() +
  ggtitle(paste("SLA <= ",iSla))

ggplot(capacitor, aes(y = heuristic)) +
  geom_point(size=4, aes(colour=heuristic, x = PREDICT), shape=21, fill="white") +
  geom_point(size=4, aes(colour=heuristic, x = EXEC)) +
  geom_text (aes(x = PREDICT, label = PREDICT, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  geom_text (aes(x = EXEC, label = EXEC, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  facet_grid(. ~ workload) +
  scale_x_discrete("Preditcion",breaks=c(0,14,21,28)) +
  scale_y_discrete("heuristic") +
  ggtitle(paste("SLA <= ",iSla)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

iSla <- 30000
capacitor <- subset(roc, sla == iSla)
graph_base = data.frame(capacitor$heuristic, capacitor$workload, capacitor$PREDICT)
graph_base$type <- "Prediction"
colnames(graph_base) <- c("heuristic", "workload", "exec", "type")
graph_base_2 = data.frame(capacitor$heuristic, capacitor$workload, capacitor$EXEC)
graph_base_2$type <- "Execution"
colnames(graph_base_2) <- c("heuristic", "workload", "exec", "type")
graph_base <- rbind(graph_base, graph_base_2)

ggplot(graph_base, aes(x = heuristic)) +
  facet_grid(. ~ workload)+
  geom_bar(stat="identity", position="stack", aes(y=exec, fill=type)) +
  coord_flip() +
  ggtitle(paste("SLA <= ",iSla))

ggplot(capacitor, aes(y = heuristic)) +
  geom_point(size=4, aes(colour=heuristic, x = PREDICT), shape=21, fill="white") +
  geom_point(size=4, aes(colour=heuristic, x = EXEC)) +
  geom_text (aes(x = PREDICT, label = PREDICT, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  geom_text (aes(x = EXEC, label = EXEC, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  facet_grid(. ~ workload) +
  scale_x_discrete("Preditcion",breaks=c(0,14,21,28)) +
  scale_y_discrete("heuristic") +
  ggtitle(paste("SLA <= ",iSla)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )


iSla <- 40000
capacitor <- subset(roc, sla == iSla)
graph_base = data.frame(capacitor$heuristic, capacitor$workload, capacitor$PREDICT)
graph_base$type <- "Prediction"
colnames(graph_base) <- c("heuristic", "workload", "exec", "type")
graph_base_2 = data.frame(capacitor$heuristic, capacitor$workload, capacitor$EXEC)
graph_base_2$type <- "Execution"
colnames(graph_base_2) <- c("heuristic", "workload", "exec", "type")
graph_base <- rbind(graph_base, graph_base_2)

ggplot(graph_base, aes(x = heuristic)) +
  facet_grid(. ~ workload)+
  geom_bar(stat="identity", position="stack", aes(y=exec, fill=type)) +
  coord_flip() +
  ggtitle(paste("SLA <= ",iSla))

ggplot(capacitor, aes(y = heuristic)) +
  geom_point(size=4, aes(colour=heuristic, x = PREDICT), shape=21, fill="white") +
  geom_point(size=4, aes(colour=heuristic, x = EXEC)) +
  geom_text (aes(x = PREDICT, label = PREDICT, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  geom_text (aes(x = EXEC, label = EXEC, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  facet_grid(. ~ workload) +
  scale_x_discrete("Preditcion",breaks=c(0,14,21,28)) +
  scale_y_discrete("heuristic") +
  ggtitle(paste("SLA <= ",iSla)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

iSla <- 50000
capacitor <- subset(roc, sla == iSla)
graph_base = data.frame(capacitor$heuristic, capacitor$workload, capacitor$PREDICT)
graph_base$type <- "Prediction"
colnames(graph_base) <- c("heuristic", "workload", "exec", "type")
graph_base_2 = data.frame(capacitor$heuristic, capacitor$workload, capacitor$EXEC)
graph_base_2$type <- "Execution"
colnames(graph_base_2) <- c("heuristic", "workload", "exec", "type")
graph_base <- rbind(graph_base, graph_base_2)

ggplot(graph_base, aes(x = heuristic)) +
  facet_grid(. ~ workload)+
  geom_bar(stat="identity", position="stack", aes(y=exec, fill=type)) +
  coord_flip() +
  ggtitle(paste("SLA <= ",iSla))

ggplot(capacitor, aes(y = heuristic)) +
  geom_point(size=4, aes(colour=heuristic, x = PREDICT), shape=21, fill="white") +
  geom_point(size=4, aes(colour=heuristic, x = EXEC)) +
  geom_text (aes(x = PREDICT, label = PREDICT, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  geom_text (aes(x = EXEC, label = EXEC, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  facet_grid(. ~ workload) +
  scale_x_discrete("Preditcion",breaks=c(0,14,21,28)) +
  scale_y_discrete("heuristic") +
  ggtitle(paste("SLA <= ",iSla)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

iSla <- 60000
capacitor <- subset(roc, sla == iSla)
graph_base = data.frame(capacitor$heuristic, capacitor$workload, capacitor$PREDICT)
graph_base$type <- "Prediction"
colnames(graph_base) <- c("heuristic", "workload", "exec", "type")
graph_base_2 = data.frame(capacitor$heuristic, capacitor$workload, capacitor$EXEC)
graph_base_2$type <- "Execution"
colnames(graph_base_2) <- c("heuristic", "workload", "exec", "type")
graph_base <- rbind(graph_base, graph_base_2)

ggplot(graph_base, aes(x = heuristic)) +
  facet_grid(. ~ workload)+
  geom_bar(stat="identity", position="stack", aes(y=exec, fill=type)) +
  coord_flip() +
  ggtitle(paste("SLA <= ",iSla))

ggplot(capacitor, aes(y = heuristic)) +
  geom_point(size=4, aes(colour=heuristic, x = PREDICT), shape=21, fill="white") +
  geom_point(size=4, aes(colour=heuristic, x = EXEC)) +
  geom_text (aes(x = PREDICT, label = PREDICT, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  geom_text (aes(x = EXEC, label = EXEC, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  facet_grid(. ~ workload) +
  scale_x_discrete("Preditcion",breaks=c(0,14,21,28)) +
  scale_y_discrete("heuristic") +
  ggtitle(paste("SLA <= ",iSla)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

iSla <- 70000
capacitor <- subset(roc, sla == iSla)
graph_base = data.frame(capacitor$heuristic, capacitor$workload, capacitor$PREDICT)
graph_base$type <- "Prediction"
colnames(graph_base) <- c("heuristic", "workload", "exec", "type")
graph_base_2 = data.frame(capacitor$heuristic, capacitor$workload, capacitor$EXEC)
graph_base_2$type <- "Execution"
colnames(graph_base_2) <- c("heuristic", "workload", "exec", "type")
graph_base <- rbind(graph_base, graph_base_2)

ggplot(graph_base, aes(x = heuristic)) +
  facet_grid(. ~ workload)+
  geom_bar(stat="identity", position="stack", aes(y=exec, fill=type)) +
  coord_flip() +
  ggtitle(paste("SLA <= ",iSla))

ggplot(capacitor, aes(y = heuristic)) +
  geom_point(size=4, aes(colour=heuristic, x = PREDICT), shape=21, fill="white") +
  geom_point(size=4, aes(colour=heuristic, x = EXEC)) +
  geom_text (aes(x = PREDICT, label = PREDICT, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  geom_text (aes(x = EXEC, label = EXEC, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  facet_grid(. ~ workload) +
  scale_x_discrete("Preditcion",breaks=c(0,14,21,28)) +
  scale_y_discrete("heuristic") +
  ggtitle(paste("SLA <= ",iSla)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

iSla <- 80000
capacitor <- subset(roc, sla == iSla)
graph_base = data.frame(capacitor$heuristic, capacitor$workload, capacitor$PREDICT)
graph_base$type <- "Prediction"
colnames(graph_base) <- c("heuristic", "workload", "exec", "type")
graph_base_2 = data.frame(capacitor$heuristic, capacitor$workload, capacitor$EXEC)
graph_base_2$type <- "Execution"
colnames(graph_base_2) <- c("heuristic", "workload", "exec", "type")
graph_base <- rbind(graph_base, graph_base_2)

ggplot(graph_base, aes(x = heuristic)) +
  facet_grid(. ~ workload)+
  geom_bar(stat="identity", position="stack", aes(y=exec, fill=type)) +
  coord_flip() +
  ggtitle(paste("SLA <= ",iSla))

ggplot(capacitor, aes(y = heuristic)) +
  geom_point(size=4, aes(colour=heuristic, x = PREDICT), shape=21, fill="white") +
  geom_point(size=4, aes(colour=heuristic, x = EXEC)) +
  geom_text (aes(x = PREDICT, label = PREDICT, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  geom_text (aes(x = EXEC, label = EXEC, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  facet_grid(. ~ workload) +
  scale_x_discrete("Preditcion",breaks=c(0,14,21,28)) +
  scale_y_discrete("heuristic") +
  ggtitle(paste("SLA <= ",iSla)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

iSla <- 90000
capacitor <- subset(roc, sla == iSla)
graph_base = data.frame(capacitor$heuristic, capacitor$workload, capacitor$PREDICT)
graph_base$type <- "Prediction"
colnames(graph_base) <- c("heuristic", "workload", "exec", "type")
graph_base_2 = data.frame(capacitor$heuristic, capacitor$workload, capacitor$EXEC)
graph_base_2$type <- "Execution"
colnames(graph_base_2) <- c("heuristic", "workload", "exec", "type")
graph_base <- rbind(graph_base, graph_base_2)

ggplot(graph_base, aes(x = heuristic)) +
  facet_grid(. ~ workload)+
  geom_bar(stat="identity", position="stack", aes(y=exec, fill=type)) +
  coord_flip() +
  ggtitle(paste("SLA <= ",iSla))

ggplot(capacitor, aes(y = heuristic)) +
  geom_point(size=4, aes(colour=heuristic, x = PREDICT), shape=21, fill="white") +
  geom_point(size=4, aes(colour=heuristic, x = EXEC)) +
  geom_text (aes(x = PREDICT, label = PREDICT, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  geom_text (aes(x = EXEC, label = EXEC, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  facet_grid(. ~ workload) +
  scale_x_discrete("Preditcion",breaks=c(0,14,21,28)) +
  scale_y_discrete("heuristic") +
  ggtitle(paste("SLA <= ",iSla)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

iSla <- 100000
capacitor <- subset(roc, sla == iSla)
graph_base = data.frame(capacitor$heuristic, capacitor$workload, capacitor$PREDICT)
graph_base$type <- "Prediction"
colnames(graph_base) <- c("heuristic", "workload", "exec", "type")
graph_base_2 = data.frame(capacitor$heuristic, capacitor$workload, capacitor$EXEC)
graph_base_2$type <- "Execution"
colnames(graph_base_2) <- c("heuristic", "workload", "exec", "type")
graph_base <- rbind(graph_base, graph_base_2)

ggplot(graph_base, aes(x = heuristic)) +
  facet_grid(. ~ workload)+
  geom_bar(stat="identity", position="stack", aes(y=exec, fill=type)) +
  coord_flip() +
  ggtitle(paste("SLA <= ",iSla))

ggplot(capacitor, aes(y = heuristic)) +
  geom_point(size=4, aes(colour=heuristic, x = PREDICT), shape=21, fill="white") +
  geom_point(size=4, aes(colour=heuristic, x = EXEC)) +
  geom_text (aes(x = PREDICT, label = PREDICT, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  geom_text (aes(x = EXEC, label = EXEC, angle = 0, hjust=0.5, vjust=1.5), size = 2.5) +
  facet_grid(. ~ workload) +
  scale_x_discrete("Preditcion",breaks=c(0,14,21,28)) +
  scale_y_discrete("heuristic") +
  ggtitle(paste("SLA <= ",iSla)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

dev.off()