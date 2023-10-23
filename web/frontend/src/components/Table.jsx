import { Box } from "@mui/material";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { tokens } from "../theme";

import { useTheme } from "@mui/material";

const Table = ({ data, columns, initialState, onRowClick, rowFunction }) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  return (
    <Box
      m="40px 0 0 0 "
      height="75vh"
      sx={{
        "& .MuiDataGrid-root": {
          border: "none",
        },
        "& .MuiDataGrid-cell": {
          borderBottom: "none",
        },
        "& .name-column--cell": {
          color: colors.greenAccent[300],
        },
        "& .MuiDataGrid-columnHeaders": {
          backgroundColor: colors.blueAccent[700],
          borderBottom: "none",
        },
        "& .MuiDataGrid-virtualScroller": {
          backgroundColor: colors.primary[400],
        },
        "& .MuiDataGrid-footerContainer": {
          borderTop: "none",
          backgroundColor: colors.blueAccent[700],
        },
        "& .MuiCheckbox-root": {
          color: `${colors.greenAccent[200]} !important`,
        },
        "& .MuiDataGrid-toolbarContainer .MuiButton-text": {
          color: `${colors.grey[100]} !important`,
        },
          '& .conflict': {
              backgroundColor: colors.redAccent[700],
              color: colors.greenAccent[300],
          },
      }}
    >
      <DataGrid
        rows={data}
        onRowClick={onRowClick}
        columns={columns}
        components={{ Toolbar: GridToolbar }}
        initialState={initialState}

        getRowClassName={rowFunction}
      />
    </Box>
  );
};

export default Table;
