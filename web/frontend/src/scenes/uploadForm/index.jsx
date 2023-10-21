import { Box } from "@mui/material";
import Header from "../../components/Header";
import Upload from "../../components/Upload";

const UploadForm = () => {
  return (
    <Box m="20px">
      <Header
        title="Add Your Data"
        subtitle="Here you can upload you collection"
      ></Header>
      <Box height="75vh">
        <Upload />
      </Box>
    </Box>
  );
};

export default UploadForm;
