import pandas as pd
import numpy as np

def weighted_median(values, weights):
    sorted_indices = np.argsort(values)
    sorted_values = values[sorted_indices]
    sorted_weights = weights[sorted_indices]

    cumulative_weights = np.cumsum(sorted_weights)
    total_weight = np.sum(sorted_weights)

    # Find the median index
    median_index = np.searchsorted(cumulative_weights, total_weight / 2.0)

    return sorted_values[median_index]

def crh_weather(dataset, iti):
    # Prepare data
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

    weight_matrix = weight[dataset[2].values-1]
    print(weight_matrix[0:5])
    # Calculate initial truth entry by entry

    stand_error=[]
    
    for entry_id in list_entry:
        val = dataset[dataset[0] == entry_id].values[0,1]
        if not val.isdigit():  # Categorical data
            same_entry = dataset[dataset[0]==entry_id]
            tempvalue = same_entry[1].values
            # Get unique value list on the same entry
            temp_list = same_entry[1].unique()
            wv = []

            # Calculate the weighted occurrence for this entry
            for k in range(len(temp_list)):
                wv.append(np.sum(((tempvalue == temp_list[k]) * weight_matrix[entry_id])))

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
    index_truth = ini_truth.copy()
    truth_matrix = pd.DataFrame(ini_truth, columns=['entry_id', 'truth'])
    std_matrix = pd.DataFrame(stand_error, columns=['entry_id', 'error'])

    # Initialize other parameters
    cat_count = np.zeros(nos)  # Count of categorical data for each source
    con_count = np.zeros(nos)  # Count of continuous data for each source

    # CRH iteration
    for i in range(iti):
        score1 = np.zeros(nos)
        score2 = np.zeros(nos)

        # Update weight
        
        for j in range(len(dataset)):
            print(dataset.loc[j])
            val = dataset.loc[j].values[1]
            if not val.isdigit():
           # if pd.api.types.is_string_dtype(dataset.at[j, 1]):  # Categorical data
                score1[dataset.at[j, 2]] += int(truth_matrix[j] != dataset.at[j, 1])

                if i == 2:
                    cat_count[dataset.at[j, 2]] += 1
            else:  # Numerical data
                score2[dataset.at[j, 2]] += np.abs((pd.to_numeric(dataset.at[j, 1]) - truth_matrix[j]) / std_matrix[j])

                if i == 2:
                    con_count[dataset.at[j, 2]] += 1

        score1 /= cat_count
        score2 /= con_count
        score1 /= np.sum(score1)
        score2 /= np.sum(score2)

        # Sum up the distance for categorical and continuous data
        score = score1 + score2

        # Calculate weight for sources
        norm_score = np.max(score)
        w = score / norm_score
        weight = -np.log(w) + 0.00001
        weight_matrix = weight[dataset[2].values]

        # Update truth
        for j in range(noe):
            if pd.api.types.is_string_dtype(dataset.at[entry_ia[j], 1]):  # Categorical data
                tempvalue = dataset.loc[entry_ia[j]:entry_ia[j + 1] - 1, 1]
                temp_list = tempvalue.unique()
                length_list = len(temp_list)

                wv = []
                for k in range(length_list):
                    wv.append(np.sum((tempvalue == temp_list[k]) * weight_matrix[entry_ia[j]:entry_ia[j + 1]]))

                I = np.argmax(wv)
                index_truth.at[j + 1, 2] = temp_list[I]

            else:  # Numerical data
                tempvalue = pd.to_numeric(dataset.loc[entry_ia[j]:entry_ia[j + 1] - 1, 1])
                tempweight = weight_matrix[entry_ia[j]:entry_ia[j + 1]]

                # Update truth by weighted median
                index_truth.at[j + 1, 2] = weighted_median(tempvalue.values, tempweight)

        truth_matrix = index_truth.loc[dataset[0].values].reset_index(drop=True)[2]

    weight = pd.DataFrame({'Source ID': list_source, 'Weight': weight})

    return index_truth, weight, ini_truth

# Example usage:
# Replace 'your_dataset.csv' with the actual path to your dataset CSV file
dataset = pd.read_csv('/Users/artur/rnd/git/fitsinn/prototype/weather_data_set.txt', header=None, sep="\t")
#print(dataset.head())
iti = 3
result_index_truth, result_weight, result_ini_truth = crh_weather(dataset, iti)
print(result_index_truth)
print(result_weight)
print(result_ini_truth)
