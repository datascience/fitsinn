import {Box, Button} from "@mui/material";
import Header from "../../components/Header";
import Upload from "../../components/Upload";
import TextField from "@mui/material/TextField";
import React from "react";
import {useContext, useState} from "react";


const UploadForm = () => {

    const [newDataset, setNewDataset] = useState('');

    const handleTextInputChange = event => {
        setNewDataset(event.target.value);
    };




  return (
    <Box m="20px">
      <Header
        title="Add Your Data"
        subtitle="Here you can upload you collection"
      ></Header>

        <Box  marginBottom="20px" display="flex" justifyContent="left">
            <TextField id="outlined-basic" label="Target Dataset"  defaultValue="default"
                       onChange = {handleTextInputChange}/>
        </Box>

      <Box height="75vh">
        <Upload dataset={newDataset} />
      </Box>
    </Box>
  );
};

export default UploadForm;
