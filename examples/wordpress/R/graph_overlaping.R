setwd("C:/Users/Matheus/VMS/crawler/repos/crawler/examples/wordpress/R")

valores2 = read.csv("summarized_price.csv", header = TRUE, stringsAsFactors = FALSE)
str(valores2)

valores2$instances <- factor(valores2$instances, order=TRUE)
valores2$workload <- factor(valores2$workload, order=TRUE)
valores2$instance_price <- factor(valores2$instance_price, order=TRUE)
valores2$total_price <- factor(valores2$total_price, order=TRUE)
valores2$price_provider_id <- mapply(function(x,y){paste(x,y,sep="_")}, valores2$instance_price, valores2$provider_id)
valores2$price_provider_id <- factor(valores2$price_provider_id, order=TRUE)
str(valores2)
#valores2$provider_id <- factor(valores2$provider_id, levels = c("m3_m", "c3_l", "m3_l","c3_xl", "m3_xl", "c3_2xl", "m3_2xl"))
valores2$provider_id <- factor(valores2$provider_id, levels = c("m3_m", "m3_l", "m3_xl", "m3_2xl", "c3_l","c3_xl", "c3_2xl"))
valores2$fc_cost_user <- factor(valores2$cost_user, order=TRUE)
#valores2$provider_id <- factor(valores2$provider_id, levels = c("m3_2xl", "c3_2xl", "m3_xl","c3_xl","m3_l" ,"c3_l" , "m3_m"))


pdf("Plot-sla.pdf", width = 9.5, height = 6)

valores2$metsla[valores2$percentile <= 5000] <- "Y"
valores2$metsla[valores2$percentile > 5000] <- "N"
newdata <- valores2[order(valores2$instances, decreasing = TRUE), ]
ggplot(newdata, aes(x=workload, y=provider_id)) + 
  geom_point(colour="black", aes(size=instances, position=instances, fill=metsla), shape=21) + 
  scale_size_manual(name="# Instances", values = c(5, 10, 15, 20))  + 
  scale_fill_manual(name= "Met SLA \n <= 5000", values = c("#f2bcb5", "#93f298")) + 
  scale_x_discrete("Workload") +
  scale_y_discrete("Profile") +  
  theme(
    axis.title.x  = element_text(face="bold"),
    axis.title.y  = element_text(face="bold")
  )

valores2$metsla[valores2$percentile <= 10000] <- "Y"
valores2$metsla[valores2$percentile > 10000] <- "N"
newdata <- valores2[order(valores2$instances, decreasing = TRUE), ]
ggplot(newdata, aes(x=workload, y=provider_id)) + 
  geom_point(colour="black", aes(size=instances, position=instances, fill=metsla), shape=21) + 
  scale_size_manual(name="# Instances", values = c(5, 10, 15, 20))  + 
  scale_fill_manual(name= "Met SLA \n <= 10000", values = c("#f2bcb5", "#93f298")) + 
  scale_x_discrete("Workload") +
  scale_y_discrete("Profile") +  
  theme(
    axis.title.x  = element_text(face="bold"),
    axis.title.y  = element_text(face="bold")
  )

valores2$metsla[valores2$percentile <= 15000] <- "Y"
valores2$metsla[valores2$percentile > 15000] <- "N"
newdata <- valores2[order(valores2$instances, decreasing = TRUE), ]
ggplot(newdata, aes(x=workload, y=provider_id)) + 
  geom_point(colour="black", aes(size=instances, position=instances, fill=metsla), shape=21) + 
  scale_size_manual(name="# Instances", values = c(5, 10, 15, 20))  + 
  scale_fill_manual(name= "Met SLA \n <= 15000", values = c("#f2bcb5", "#93f298")) + 
  scale_x_discrete("Workload") +
  scale_y_discrete("Profile") +  
  theme(
    axis.title.x  = element_text(face="bold"),
    axis.title.y  = element_text(face="bold")
  )

valores2$metsla[valores2$percentile <= 20000] <- "Y"
valores2$metsla[valores2$percentile > 20000] <- "N"
newdata <- valores2[order(valores2$instances, decreasing = TRUE), ]
ggplot(newdata, aes(x=workload, y=provider_id)) + 
  geom_point(colour="black", aes(size=instances, position=instances, fill=metsla), shape=21) + 
  scale_size_manual(name="# Instances", values = c(5, 10, 15, 20))  + 
  scale_fill_manual(name= "Met SLA \n <= 20000", values = c("#f2bcb5", "#93f298")) + 
  scale_x_discrete("Workload") +
  scale_y_discrete("Profile") +  
  theme(
    axis.title.x  = element_text(face="bold"),
    axis.title.y  = element_text(face="bold")
  )

valores2$metsla[valores2$percentile <= 25000] <- "Y"
valores2$metsla[valores2$percentile > 25000] <- "N"
newdata <- valores2[order(valores2$instances, decreasing = TRUE), ]
ggplot(newdata, aes(x=workload, y=provider_id)) + 
  geom_point(colour="black", aes(size=instances, position=instances, fill=metsla), shape=21) + 
  scale_size_manual(name="# Instances", values = c(5, 10, 15, 20))  + 
  scale_fill_manual(name= "Met SLA \n <= 25000", values = c("#f2bcb5", "#93f298")) + 
  scale_x_discrete("Workload") +
  scale_y_discrete("Profile") +  
  theme(
    axis.title.x  = element_text(face="bold"),
    axis.title.y  = element_text(face="bold")
  )

valores2$metsla[valores2$percentile <= 30000] <- "Y"
valores2$metsla[valores2$percentile > 30000] <- "N"
newdata <- valores2[order(valores2$instances, decreasing = TRUE), ]
ggplot(newdata, aes(x=workload, y=provider_id)) + 
  geom_point(colour="black", aes(size=instances, position=instances, fill=metsla), shape=21) + 
  scale_size_manual(name="# Instances", values = c(5, 10, 15, 20))  + 
  scale_fill_manual(name= "Met SLA \n <= 30000", values = c("#f2bcb5", "#93f298")) + 
  scale_x_discrete("Workload") +
  scale_y_discrete("Profile") +  
  theme(
    axis.title.x  = element_text(face="bold"),
    axis.title.y  = element_text(face="bold")
  )

valores2$metsla[valores2$percentile <= 35000] <- "Y"
valores2$metsla[valores2$percentile > 35000] <- "N"
newdata <- valores2[order(valores2$instances, decreasing = TRUE), ]
ggplot(newdata, aes(x=workload, y=provider_id)) + 
  geom_point(colour="black", aes(size=instances, position=instances, fill=metsla), shape=21) + 
  scale_size_manual(name="# Instances", values = c(5, 10, 15, 20))  + 
  scale_fill_manual(name= "Met SLA \n <= 35000", values = c("#f2bcb5", "#93f298")) + 
  scale_x_discrete("Workload") +
  scale_y_discrete("Profile") +  
  theme(
    axis.title.x  = element_text(face="bold"),
    axis.title.y  = element_text(face="bold")
  )

dev.off()

valores2$passed[valores2$percentile <= 20000] <- "PASSOU"
valores2$passed[valores2$percentile > 20000] <- "PERDEU"
valores2$passed[valores2$percentile > 30000 & valores2$percentile <= 40000] <- "PERDEU"
valores2$passed[valores2$percentile > 40000] <- "PERDEU-PRE"

newdata <- valores2[order(valores2$instances, decreasing = TRUE), ]
ggplot(newdata, aes(x=workload, y=provider_id)) + 
  geom_point(colour="black", aes(size=instances, position=instances, fill=passed), shape=21) + 
  scale_size_manual(values=c(5,10, 15,20))  + 
  scale_fill_manual(values = c("#93f298", "#f2bcb5")) +
  scale_x_discrete("Workload") +
  scale_y_discrete("Profile") 

newdata$instances_plus[newdata$instances == 1] <- 6
newdata$instances_plus[newdata$instances == 2] <- 11
newdata$instances_plus[newdata$instances == 3] <- 16
newdata$instances_plus[newdata$instances == 4] <- 21

pdf("Plot5.pdf", width = 9.5, height = 6)
ggplot(newdata, aes(x=workload, y=provider_id)) + geom_point(aes(size=instances, colour=passed, position=instances)) + geom_point(shape = 1,size = newdata$instances_plus ,colour = "white")+ scale_size_manual(values=c(5,10, 15,20))  + scale_color_manual(values = c("#93f298","#009B58", "#D5422D", "#f2bcb5"))
ggplot(newdata, aes(x=workload, y=provider_id)) + geom_point(shape = 15, aes(size=instances, colour=passed, position=instances)) + geom_point(shape = 0,size = newdata$instances_plus ,colour = "white")+ scale_size_manual(values=c(5,10, 15,20)) + scale_color_manual(values = c("#93f298","#009B58", "#D5422D", "#f2bcb5"))
dev.off()

library(ggplot2)
pdf("Plot3.pdf", width = 9.5, height = 6)
ggplot(newdata, aes(x=workload, y=provider_id)) + geom_point(aes(size=instances, colour=instances, position=instances)) + scale_size_manual(values=c(5,10, 15,20)) + scale_color_manual(values = c("#109618","#990099","#FF9900","#DC3912"))
ggplot(newdata, aes(x=workload, y=provider_id)) + geom_point(shape = 15, aes(size=instances, colour=instances, position=instances)) + scale_size_manual(values=c(5,10, 15,20))
ggplot(newdata, aes(x=workload, y=provider_id)) + geom_point(aes(size=instances, colour=passed, position=instances)) + scale_size_manual(values=c(5,10, 15,20))
ggplot(newdata, aes(x=workload, y=provider_id)) + geom_point(shape = 1, aes(size=instances, colour=passed, position=instances)) + scale_size_manual(values=c(5,10, 15,20))
ggplot(newdata, aes(x=workload, y=provider_id)) + geom_point(aes(shape = instances, size=instances, colour=passed, position=instances)) + scale_size_manual(values=c(5,10, 15,20)) + scale_shape_manual(values=c(16,17, 18,15))
ggplot(newdata, aes(x=workload, y=provider_id)) + geom_point(aes(shape = instances, size=instances, colour=passed, position=instances)) + scale_size_manual(values=c(5,10, 15,20)) + scale_shape_manual(values=c(6,1,2,1))
dev.off()
