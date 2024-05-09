import {Box, Button} from "@mui/material";
import Header from "../../components/Header";
import Upload from "../../components/Upload";
import TextField from "@mui/material/TextField";
import React from "react";
import {useContext, useState} from "react";


const UploadForm = () => {
   const createDataset = (name) => {
       console.log(textInput);
   }

    const [textInput, setTextInput] = useState('');

    const handleTextInputChange = event => {
        setTextInput(event.target.value);
    };




  return (
    <Box m="20px">
      <Header
        title="Add Your Data"
        subtitle="Here you can upload you collection"
      ></Header>

        <Box  marginBottom="20px" display="flex" justifyContent="left">
            <TextField id="outlined-basic" label="Name"  value= {textInput}
                       onChange= {handleTextInputChange}/>
            <Button onClick={createDataset} color="secondary" variant="contained">
                Create Dataset
            </Button>
        </Box>

      <Box height="75vh">
        <Upload />
      </Box>
    </Box>
  );
};

export default UploadForm;
