setwd("C:/Users/Matheus/VMS/crawler/repos/crawler/examples/wordpress/R")


pdf("ROC-ALL.pdf", width = 9.5, height = 6)

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

ggplot(roc, aes(y = PPV, x = TPR)) +  
  geom_point(size=3, colour="black") +
  facet_grid(. ~ workload) +
  geom_text (aes(label = heuristic, angle = 0, hjust=0.5, vjust=-0.5), size = 3.5) +
  scale_y_continuous("Precision",limits=c(0, 1)) +
  scale_x_continuous("Recal",limits=c(0, 1)) +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    axis.title.x  = element_text(face="bold"),
    axis.title.y  = element_text(face="bold")
  )


roc = read.csv("cap_result.csv", header = TRUE, stringsAsFactors = FALSE)
roc$workload <- factor(roc$workload, order=TRUE)

ggplot(roc, aes(x = workload, y = F_MEASURE)) +
  geom_point(size=4, aes(colour=heuristic), shape=21, fill="white") +  
  geom_line(aes(group=heuristic, colour=heuristic), linetype="dashed") +
  geom_text (aes(label = heuristic, angle = 0, hjust=0.5, vjust=-0.5), size = 3.5) +  
  scale_x_discrete("Workload") +
  scale_y_continuous("F-Measure",limits=c(0, 1)) +
  ggtitle("SLA <= 10000") +
  theme_bw(base_size = 12, base_family = "") +
  theme(
    title    = element_text(face="bold", size = 14),
    axis.title  = element_text(face="bold", size = 12)
  )

dev.off()


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
  
  if ((result$TP+result$FP) != 0){
    result$PPV <- result$TP/(result$TP+result$FP)
  }else if(result$TP == 0){
    result$PPV <- 1
  }else{
    result$PPV <- 0
  }
  
  if (result$P != 0){
    result$TPR <- result$TP/result$P
  }else if(result$TP == 0){
    result$TPR <- 1
  }else{
    result$TPR <- 0
  }
  
  result$F_MEASURE <- 2 * ((result$PPV * result$TPR)/(result$PPV + result$TPR))
  if(result$N != 0){
    result$FPR <- result$FP/result$N
  }else if(result$FP == 0){
    result$FPR <- 1
  }else{
    result$FPR <- 0
  }
  result$PREDICT <- sum(data$predict_num)
  result$EXEC <- nrow(data) - result$PREDICT
  return(result)
}



calc_performance <- function(file, pSla){
  capacitor = read.csv(file, header = TRUE, stringsAsFactors = FALSE)
  oracle = read.csv("summarized_price.csv", header = TRUE, stringsAsFactors = FALSE)
  
  ##Subsetting the capacitor
  capacitor <- subset(capacitor, sla == pSla)
  
  ##Same number of rows?
  valid <- nrow(oracle) == nrow(capacitor)
  valid
  ##Ensuring the order!
  
  oracle <- oracle[order(oracle$workload, oracle$configuration),]
  capacitor <- capacitor[order(capacitor$workload, capacitor$configuration),]
  oracle$metsla[oracle$percentile <= pSla] <- T
  oracle$metsla[oracle$percentile > pSla] <- F
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
  
  summary <- capacitor_summary(subset(capacitor, workload == 100))
  summary <- rbind(summary, capacitor_summary(subset(capacitor, workload == 200)))
  summary <- rbind(summary, capacitor_summary(subset(capacitor, workload == 300)))
  summary <- rbind(summary, capacitor_summary(subset(capacitor, workload == 400)))
  summary <- rbind(summary, capacitor_summary(subset(capacitor, workload == 500)))
  summary <- rbind(summary, capacitor_summary(subset(capacitor, workload == 600)))
  summary <- rbind(summary, capacitor_summary(subset(capacitor, workload == 700)))
  summary <- rbind(summary, capacitor_summary(subset(capacitor, workload == 800)))
  summary <- rbind(summary, capacitor_summary(subset(capacitor, workload == 900)))
  summary <- rbind(summary, capacitor_summary(subset(capacitor, workload == 1000)))
  
  return(summary)
}


#Before start
# sed -i 's;,\([1-4]\)\.\([mc]\);,\1_\2;g' *.csv
# sed -i 's;true;TRUE;g' *.csv
# sed -i 's;false;FALSE;g' *.csv

files <- c("CC_heuristic_result.csv","CO_heuristic_result.csv","CP_heuristic_result.csv","CR_heuristic_result.csv","OC_heuristic_result.csv","OO_heuristic_result.csv","OP_heuristic_result.csv","OR_heuristic_result.csv","PC_heuristic_result.csv","PO_heuristic_result.csv","PP_heuristic_result.csv","PR_heuristic_result.csv","RC_heuristic_result.csv","RO_heuristic_result.csv","RP_heuristic_result.csv","RR_heuristic_result.csv")
slas <- seq(10000, 100000, by=10000)
results <- c()

for (sFile in files){
  for (iSla in slas){
    results <- rbind(results, calc_performance(sFile, iSla))
  }
}

write.table(results, "cap_result.csv", sep=",")

subset(results, sla==30000 & heuristic=="CO")

results <- rbind(results, calc_performance("RO_heuristic_result.csv", 10000))
