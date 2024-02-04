import { Box } from "@mui/material";
import Filter from "../../components/Filter";
import Header from "../../components/Header";

const FilterPage = () => {
  return (
    <Box m="20px">
      <Header
        title="Filter Configuration"
        subtitle="You can construct you filter here"
      ></Header>
      <Box height="75vh">
        <Filter />
      </Box>
    </Box>
  );
};

export default FilterPage;
