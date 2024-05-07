import {Box, Button, IconButton, Typography, useTheme} from "@mui/material";
import React, { useContext } from "react";
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select, { SelectChangeEvent } from '@mui/material/Select';

import { ColorModeContext, tokens } from "../../theme";

import Filter from "../../components/Filter";
const Topbar = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const colorMode = useContext(ColorModeContext);
  return (
    <Box  m="20px"  display="flex" justifyContent="space-between" >
      <Filter />
      <Box   >
      <FormControl sx={{ m: 1, minWidth: 120 }}>
        <InputLabel>Dataset</InputLabel>
        <Select label="Dataset">
          <MenuItem value={10}>Ten</MenuItem>
          <MenuItem value={20}>Twenty</MenuItem>
          <MenuItem value={30}>Thirty</MenuItem>
        </Select>
      </FormControl>
      </Box>
    </Box>

  );
};

export default Topbar;
