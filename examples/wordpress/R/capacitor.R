setwd("M:/users/matheus/crawler/repos/crawler/examples/wordpress/R")
capacitor = read.csv("capacitor.csv", header = TRUE, stringsAsFactors = FALSE)
str(capacitor)
oracle = read.csv("summarized_price.csv", header = TRUE, stringsAsFactors = FALSE)
str(oracle)

##Subsetting the oracle
oracle <- subset(oracle, workload <= 200 & instances <= 2)

##Same number of rows?
valid <- nrow(oracle) == nrow(capacitor)
valid
##Ensuring the order!
oracle <- oracle[order(oracle$workload, oracle$configuration),]
capacitor <- capacitor[order(capacitor$workload, capacitor$configuration),]
oracle$metsla[oracle$percentile <= 20000] <- T
oracle$metsla[oracle$percentile > 20000] <- F
capacitor$oracle <- oracle$metsla

capacitor$metsla_num[capacitor$metsla] <- 1
capacitor$metsla_num[!capacitor$metsla] <- 0
capacitor$metsla_num[is.na(capacitor$metsla) & capacitor$oracle] <- 0
#capacitor$metsla_num[is.na(capacitor$metsla) & !capacitor$oracle] <- 1

capacitor$oracle_num[capacitor$oracle] <- 1
capacitor$oracle_num[!capacitor$oracle] <- 0

capacitor$predict_num[capacitor$predict] <- 1
capacitor$predict_num[!capacitor$predict] <- 0
capacitor$predict_num[is.na(capacitor$predict)] <- 0

capacitor <- subset(capacitor, !is.na(metsla))

str(capacitor)

verify_predictions <- function(){
  TP <- 0
  TN <- 0
  FP <- 0
  FN <- 0
  
  predict <- function(predicted, label){
    if(label == 1){
      if(predicted == label){
        TP <<- TP + 1
      }else{
        FN <<- FN + 1
      }
    }else{
      if(predicted == label){
        TN <<- TN + 1
      }else{
        FP <<- FP + 1
      }
    }
    
    return(data.frame(TP=c(TP),TN=c(TN),FP=c(FP),FN=c(FN)))    
  }
  
  return(predict)
}



capacitor_summary <- function(data){
  total <- nrow(data)
  positive <- sum(data$oracle_num)
  negative <- total - positive
  verify <- verify_predictions()
  summary <- mapply(verify, data$metsla_num, data$oracle_num)
  
  sum_last_element <- summary[, total]
  
  result=data.frame(heuristic=data[1,]$heuristic, workload=data[1,]$workload, sla=data[1,]$sla, P=c(positive),N=c(negative),TP=c(sum_last_element$TP),TN=c(sum_last_element$TN),FP=c(sum_last_element$FP),FN=c(sum_last_element$FN))  
  result$PPV <- result$TP/(result$TP+result$FP)
  result$TPR <- result$TP/result$P
  result$F_MEASURE <- 2 * ((result$PPV * result$TPR)/(result$PPV + result$TPR))
  result$FPR <- result$FP/result$N
  result$PREDICT <- sum(data$predict_num)
  result$EXEC <- nrow(data) - result$PREDICT
  return(result)
}
summary <- capacitor_summary(subset(capacitor, workload == 100))
summary <- rbind(summary, capacitor_summary(subset(capacitor, workload == 200)))
write.table(summary, "cap_result_1.csv", sep=",")

pdf("ROC.pdf", width = 9.5, height = 6)

roc = read.csv("cap_result.csv", header = TRUE, stringsAsFactors = FALSE)
str(roc)
require(ggplot2)

#ROC - Curve
ggplot(roc, aes(x = FPR, y = TPR)) +  
  geom_point(size=3, colour="black") +
  facet_grid(. ~ workload) +
  geom_text (aes(label = heuristic, angle = 0, hjust=0, vjust=-0.5), size = 3.5) +
  scale_x_continuous("False Positive Rate (1-Specificity)",limits=c(0, 1)) +
  scale_y_continuous("True Positive Rate (Sensitivity)",limits=c(0, 1)) +
  geom_abline (intercept = 0, slope = 1) +    
  theme_bw(base_size = 12, base_family = "") +
  theme(
    axis.title.x  = element_text(face="bold"),
    axis.title.y  = element_text(face="bold")
    )


roc = read.csv("cap_result.csv", header = TRUE, stringsAsFactors = FALSE)
roc$workload <- factor(roc$workload, order=TRUE)

ggplot(roc, aes(x = workload, y = F_MEASURE)) +
  geom_point(size=3, colour="black") +
  geom_text (aes(label = heuristic, angle = 0, hjust=0, vjust=-0.5), size = 3.5) +  
  scale_x_discrete("Workload") +
  scale_y_continuous("F-Measure",limits=c(0, 1)) +
  ggtitle("SLA <= 10000") +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

dev.off()