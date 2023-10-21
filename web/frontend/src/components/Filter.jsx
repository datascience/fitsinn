import { Box, Typography, useTheme } from "@mui/material";
import { Button } from "@mui/material";
import { tokens } from "../theme";
import React, { useEffect } from "react";
import { useState } from "react";
import { useTracked } from "react-tracked";
import { QueryBuilderMaterial } from "@react-querybuilder/material";
import { QueryBuilder, formatQuery, parseCEL } from "react-querybuilder";
import "react-querybuilder/dist/query-builder.css";
import { BACKEND_URL } from "../AppConfig";

const operators = [
  { name: "=", label: "=" },
  { name: "!=", label: "!=" },
  { name: "<", label: "<" },
  { name: ">", label: ">" },
  { name: "<=", label: "<=" },
  { name: ">=", label: ">=" },
];

const properties = [
  { name: "FORMAT", label: "FORMAT" },
  { name: "FORMAT_VERSION", label: "FORMAT_VERSION" },
  { name: "MIMETYPE", label: "MIMETYPE" },
  { name: "FILENAME", label: "FILENAME" },
  { name: "AUTHOR", label: "AUTHOR" },
  { name: "EXTERNALIDENTIFIER", label: "EXTERNALIDENTIFIER" },
  { name: "SIZE", label: "SIZE" },
  { name: "MD5CHECKSUM", label: "MD5CHECKSUM" },
  { name: "FSLASTMODIFIED", label: "FSLASTMODIFIED", inputType: "date" },
  { name: "FILEPATH", label: "FILEPATH" },
  { name: "CREATED", label: "CREATED", inputType: "date" },
  { name: "CREATINGAPPLICATIONNAME", label: "CREATINGAPPLICATIONNAME" },
];

const Filter = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  const [state, dispatch] = useTracked();

  //var filter = sessionStorage.getItem("filter");
  const [query, setQuery] = useState(
    state.filter ? parseCEL(state.filter) : ""
  );
  // useState(filter ? parseCEL(filter) : state.filter);

  var fields = properties;

  useEffect(
    () => {
      //if (!state.filterFields) {

      const fetchSources = async () => {
        try {
          const response = await fetch(BACKEND_URL + "/sources");
          let data = await response.json();
          let sources = data.map((prop) => ({ name: prop, label: prop }));
          dispatch({ filterSources: sources });
        } catch (error) {
          console.log(error);
        }
      };

      fetchSources();
      console.log("updating filter component state");
      setQuery(state.filter ? parseCEL(state.filter) : "");
    },
    //}
    [state.filter]
  );

  return (
    <Box
      sx={{
        display: "flex",
        justifyContent: "space-between",
        p: "2",
      }}
    >
      <Box
        sx={{
          ".ruleGroup": {
            "border-style": "none",
            backgroundColor: colors.primary[400],
            gap: "0",
            fontSize: 2,
          },
          ".MuiSelect-select, .MuiSelect-icon, .MuiInput-input": {
            color: colors.grey[100],
            fontSize: 12,
          },
          ".MuiButton-root": {
            color: colors.grey[100],
            backgroundColor: colors.blueAccent[700],
            fontSize: 12,
            p: 0,
            "min-width": 50,
          },
        }}
      >
        <QueryBuilderMaterial style={{ color: "green", ".rqb-spacing": 0.5 }}>
          <QueryBuilder
            fields={fields}
            operators={operators}
            query={query}
            onQueryChange={(q) => setQuery(q)}
          />
        </QueryBuilderMaterial>
      </Box>
      <Box
        sx={{
          ".MuiButton-root": {
            color: colors.grey[100],
            backgroundColor: colors.blueAccent[700],
          },
        }}
      >
        <Button
          onClick={() => {
            if (query == "") {
              return;
            }
            let stringQuery = formatQuery(query, "cel");
            console.log(stringQuery);
            //sessionStorage.setItem("filter", stringQuery);
            if (stringQuery === "1 == 1") {
              dispatch({ filter: "" });
            } else {
              dispatch({ filter: stringQuery });
            }
          }}
        >
          <Typography variant="h5" fontWeight="600">
            Set filter
          </Typography>
        </Button>
      </Box>
    </Box>
  );
};

export default Filter;
