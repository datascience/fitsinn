import { Box, IconButton, useTheme } from "@mui/material";
import { useContext } from "react";

import { ColorModeContext, tokens } from "../../theme";

import Filter from "../../components/Filter";
const Topbar = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const colorMode = useContext(ColorModeContext);
  return (
    <Box m="20px">
      <Filter />
    </Box>
  );
};

export default Topbar;
