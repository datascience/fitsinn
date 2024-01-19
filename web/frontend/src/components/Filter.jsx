import {Box, useTheme} from "@mui/material";
import {tokens} from "../theme";
import React, {useEffect, useState} from "react";
import {QueryBuilderMaterial} from "@react-querybuilder/material";
import {formatQuery, parseCEL, QueryBuilder} from "react-querybuilder";
import "react-querybuilder/dist/query-builder.css";
import {useSessionStorage} from "@uidotdev/usehooks";

const operators = [
  { name: "=", label: "=" },
  { name: "!=", label: "!=" },
  { name: "<", label: "<" },
  { name: ">", label: ">" },
  { name: "<=", label: "<=" },
  { name: ">=", label: ">=" },
];

var properties = [
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

export const dateProperties = ["FSLASTMODIFIED", "CREATED", "LASTMODIFIED"];

const Filter = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const [filterString, setFilterString] = useSessionStorage("filterString", "");
  const [globalProperties, setGlobalProperties] = useSessionStorage(
    "globalProperties",
    []
  );

  const [filter, setFilter] = useState(
    filterString ? parseCEL(filterString) : ""
  );

  useEffect(() => {
    properties = globalProperties.map((prop) => {
      if (dateProperties.includes(prop)) {
        return { name: prop, label: prop, inputType: "date" };
      } else {
        return { name: prop, label: prop };
      }
    });
    console.log("updating filter component state");
  }, [globalProperties]);

  const updateFilter = (q) => {
    if (q == "") {
      return;
    }
    let stringQuery = formatQuery(q, "cel");
    console.log(stringQuery);
    if (stringQuery === "1 == 1" || stringQuery.endsWith('== ""')) {
      setFilterString("");
    } else {
      setFilterString(stringQuery);
    }
    setFilter(q);
  };

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
            fields={properties}
            operators={operators}
            query={filterString ? parseCEL(filterString) : ""}
            onQueryChange={updateFilter}
          />
        </QueryBuilderMaterial>
      </Box>
    </Box>
  );
};

export default Filter;
