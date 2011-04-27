## EXPERIMENT 1

RESOURCE=resources/eval/geoclef
SRC=src/renoir/test/geoclef

# generate queries for baseline GeoCLEF
groovy ${SRC}/querygeneration/GeoCLEF_All_Baseline_NoQE_QueryGeneration_Test.groovy

# generate runs for baseline GeoCLEF
groovy ${SRC}/rungeneration/GeoCLEF_All_Baseline_NoQE_StandardBM25_RunGeneration_Test.groovy 
groovy ${SRC}/rungeneration/GeoCLEF_All_Baseline_NoQE_OptimisedBM25_RunGeneration_Test.groovy 

# Evaluate them

for year in 2005 2006 2007 2008
do 
trec_eval -q -a ${RESOURCE}/qrels/qrelsGeoCLEFEN${year}.txt ${RESOURCE}/runs/GeoCLEF_EN_${year}_Baseline_noQE_StandardBM25.run >> ${RESOURCE}/logs/GeoCLEF_EN_${year}_Baseline_noQE_StandardBM25.run.log 
echo "Map for GeoCLEF_EN_${year}_Baseline_noQE_StandardBM25: "
grep map ${RESOURCE}/logs/GeoCLEF_EN_${year}_Baseline_noQE_StandardBM25.run.log 
trec_eval -q -a ${RESOURCE}/qrels/qrelsGeoCLEFEN${year}.txt ${RESOURCE}/runs/GeoCLEF_EN_${year}_Baseline_noQE_OptimisedBM25.run >> ${RESOURCE}/logs/GeoCLEF_EN_${year}_Baseline_noQE_OptimisedBM25.run.log 
echo "Map for GeoCLEF_EN_${year}_Baseline_noQE_OptimisedBM25: "
grep map ${RESOURCE}/logs/GeoCLEF_EN_${year}_Baseline_noQE_OptimisedBM25.run.log 
done

for year in 2006 2007 2008
do 
trec_eval -q -a ${RESOURCE}/qrels/qrelsGeoCLEFPT${year}.txt ${RESOURCE}/run/GeoCLEF_PT_${year}_Baseline_noQE_StandardBM25.run >> ${RESOURCE}/logs/GeoCLEF_PT_${year}_Baseline_noQE_StandardBM25.run.log 
echo "Map for GeoCLEF_PT_${year}_Baseline_noQE_StandardBM25: "
grep map ${RESOURCE}/logs/GeoCLEF_PT_${year}_Baseline_noQE_StandardBM25.run.log 
trec_eval -q -a ${RESOURCE}/qrels/qrelsGeoCLEFPT${year}.txt ${RESOURCE}/geoclef/run/GeoCLEF_PT_${year}_Baseline_noQE_OptimisedBM25.run >> ${RESOURCE}/logs/GeoCLEF_PT_${year}_Baseline_noQE_OptimisedBM25.run.log 
echo "Map for GeoCLEF_PT_${year}_Baseline_noQE_OptimisedBM25: "
grep map ${RESOURCE}/logs/GeoCLEF_PT_${year}_Baseline_noQE_OptimisedBM25.run.log 
done