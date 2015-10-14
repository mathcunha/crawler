setwd("C:/Users/Matheus/VMS/crawler/repos/crawler/examples/terasort")

results = read.csv("results_perf.csv", header = TRUE, stringsAsFactors = FALSE)

str(results)

my_quantile <- function(x){quantile(x, c(.90))}
library(doBy)
valores_mem <- summaryBy(results$memused ~ results$scenario + results$workload, results, FUN = c(my_quantile))
valores_cpu <- summaryBy(results$cpuused ~ results$scenario + results$workload, results, FUN = c(my_quantile))

valores_merged <- merge(valores_mem, valores_cpu, by = c("scenario","workload"))
valores_merged <- merge(valores_merged, read.csv("results.csv", header = TRUE, stringsAsFactors = FALSE), by = c("scenario","workload"))

valores_merged$instances <- sapply(valores_merged$scenario, function(x) {
  unlist(strsplit(x,"_"))[1]
})
valores_merged$provider_id <- sapply(valores_merged$scenario, function(x) {
  provider <- unlist(strsplit(x,"_"))
  paste(provider[2],provider[3],sep=".")
})

valores_merged$execution <- valores_merged$fim - valores_merged$ini

write.table(valores_merged, "r_terasort_cpu_mem.csv", sep=",")
