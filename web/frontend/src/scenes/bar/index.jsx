import { Box } from "@mui/material";
import Header from "../../components/Header";
import { propertyValues as data } from "../../data/procontentData";
import PropertyValueDistribution from "../PropertyValueDistribution";

const Bar = () => {
  return (
    <Box m="20px">
      <Header title="Bar Chart" subtitle="Simple Bar Chart"></Header>
      <Box height="75vh">
        <PropertyValueDistribution property={"format"} />
      </Box>
    </Box>
  );
};

export default Bar;
