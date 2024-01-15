import pandas as pd
import numpy as np

def weighted_median1(values, weights):
    sorted_indices = np.argsort(values)
    sorted_values = sorted(values)
    sorted_weights = sorted(weights)

    cumulative_weights = np.cumsum(sorted_weights)
    total_weight = np.sum(sorted_weights)

    # Find the median index
    median_index = np.searchsorted(cumulative_weights, total_weight / 2.0)

    return sorted_values[median_index]


def execute(dataset, iti):
    dataset.sort_values(by=[0], inplace=True)
    
    # Calculate statistics about the data
    nof = len(dataset)
    # Get the entry list
    list_entry = dataset[0].unique()
    # Get the source list
    list_source = dataset[2].unique()
    # Number of sources
    nos = len(list_source)
    # Number of entries
    noe = len(list_entry)
    
    # Modify entry_ia so that the following works
    entry_ia = np.append(np.arange(0, noe), nof)
    
    # Initialization
    ini_truth = []
    weight = np.ones(nos) / nos
    
    weight_vector = weight[dataset[2].values-1]
    weight_matrix = pd.DataFrame(weight_vector, columns=['weight'],index = dataset.index)
    # Calculate initial truth entry by entry
    
    stand_error=[]
    
    
    for entry_id in list_entry:
        val = dataset[dataset[0] == entry_id].values[0,1]
     
        if not val.lstrip('-+').isdigit():  # Categorical data
            same_entry = dataset[dataset[0]==entry_id]
            tempvalue = same_entry[1].values
            # Get unique value list on the same entry
            temp_list = same_entry[1].unique()
            wv = []
    
            # Calculate the weighted occurrence for this entry
            for k in range(len(temp_list)):
                wv.append(np.sum(((tempvalue == temp_list[k]) * weight_matrix.loc[same_entry.index].values)))
    
            # Get the voting result
            I = np.argmax(wv)
            ini_truth.append([entry_id, temp_list[I]])
    
        else:  # Numerical data
            
            same_entry = dataset[dataset[0]==entry_id]
            
            tempvalue = same_entry[1].values
            tempvalue_int = [int(x) for x in tempvalue]
            median = np.median(tempvalue_int)
            
            # Calculate median for this entry
            ini_truth.append([entry_id, median])
            stand_error.append([entry_id,np.std(tempvalue_int) ])
    index_truth = [] #ini_truth.copy()
    truth_matrix = pd.DataFrame(ini_truth, columns=['entry_id', 'truth'])
    std_matrix = pd.DataFrame(stand_error, columns=['entry_id', 'error'])
    
    # Initialize other parameters
    cat_count = np.zeros(nos)  # Count of categorical data for each source
    con_count = np.zeros(nos)  # Count of continuous data for each source
    
    for i in range(iti):
        index_truth = []
        score1 = np.zeros(nos)
        score2 = np.zeros(nos)
    
        # Update weight
        
        for j in range(len(dataset)):
            val = dataset.loc[j].values[1]
            entry_id = dataset.loc[j].values[0]
            source_id = dataset.loc[j].values[2]
            true_val=truth_matrix[truth_matrix['entry_id'] == entry_id].values[0,1]
            if not val.lstrip('-+').isdigit():
                true_val=truth_matrix[truth_matrix['entry_id'] == entry_id].values[0,1]
                score1[source_id-1] += int(true_val != val)  #we count non-matching TypeError: weighted_median() missing 1 required positional argument: 'weight'values
                #if i == 2:
                cat_count[source_id-1] += 1
            else:  # Numerical data
                error=std_matrix[std_matrix['entry_id'] == entry_id].values[0,1]
                if (error != 0 ):
                    score2[source_id-1] += np.abs( int(val) - int(true_val)) / error
    
                #if i == 2:
                con_count[source_id-1] += 1
        score1 /= cat_count
        score2 /= con_count
        score1 /= sum(score1)
        score2 /= sum(score2)
    
        score = score1 + score2
        norm_score = np.max(score)
        w = score / norm_score
        weight = -np.log(w) + 0.00001
    
        weight_vector = weight[dataset[2].values-1]
        weight_matrix = pd.DataFrame(weight_vector, columns=['weight'],index = dataset.index)
        for entry_id in list_entry:
            val = dataset[dataset[0] == entry_id].values[0,1]
            if not val.lstrip('-+').isdigit():  # Categorical data
                same_entry = dataset[dataset[0]==entry_id]
                tempvalue = same_entry[1].values
                temp_list = same_entry[1].unique()
                wv = []
                for k in range(len(temp_list)):
                    wv.append(np.sum(((tempvalue == temp_list[k]) * weight_matrix.loc[same_entry.index].values)))
                I = np.argmax(wv)
                index_truth.append([entry_id, temp_list[I]])
            else:  # Numerical data
                same_entry = dataset[dataset[0]==entry_id]
                tempvalue = same_entry[1].values
                tempvalue_int = [int(x) for x in tempvalue]
                
                tempweight=weight_matrix.loc[same_entry.index].values
                wm = weighted_median1(tempvalue_int, tempweight)
                index_truth.append([entry_id, wm])
    
        print(len(index_truth))
        truth_matrix = pd.DataFrame(index_truth, columns=['entry_id', 'truth'])


dataset = pd.read_csv('data/weather_data_set.txt', sep="\t",  header=None)
groundtruth = pd.read_csv('data/weather_ground_truth.txt', sep="\t")
iti = 3


execute(dataset, iti)
       
 