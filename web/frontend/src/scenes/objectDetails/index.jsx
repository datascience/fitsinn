import { Box } from "@mui/material";
import Header from "../../components/Header";
import Table from "../../components/Table";
import { useTracked } from "react-tracked";
import React, { useState, useEffect } from "react";
import { BACKEND_URL } from "../../AppConfig";

const ObjectDetails = () => {
  const [state, dispatch] = useTracked();
  const initialState = {
    sorting: {
      sortModel: [{ field: "property", sort: "asc" }],
    },
  };
  const [data, setData] = useState([
    {
      id: 1,
      property: 1,
      value: 1,
      source: 1,
      filepath: "/usr/local/tomcat/webapps/fits/upload/1582118786085/README.md",
    },
  ]);
  useEffect(() => {
    console.log("loading the object details list");
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const fetchPost = async () => {
      console.log(state.objectdetails);
      try {
        var raw = JSON.stringify({
          filePath: state.objectdetails,
        });

        var requestOptions = {
          method: "POST",
          headers: myHeaders,
          body: raw,
          redirect: "follow",
        };
        const response = await fetch(
          BACKEND_URL +
            "/object?" +
            new URLSearchParams({
              filepath: state.objectdetails,
            }),
          requestOptions
        );
        const data = await response.json();

        let id = 0;
        var tmp = data.map((stat) => {
          var res = stat;
          res.id = id++;
          return res;
        });

        setData(tmp);
      } catch (error) {
        console.log(error);
      }
    };
    fetchPost();
  }, [state.objectdetails]);

  const columns = [
    {
      field: "property",
      headerName: "Property",
      flex: 0.5,
    },
    {
      field: "value",
      headerName: "Property Value",
      flex: 1,
    },
    {
      field: "valueType",
      headerName: "Value Type",
      flex: 0.5,
    },
    {
      field: "source",
      headerName: "Source Tool",
      cellClassName: "name-column--cell",
      flex: 1,
    },
  ];

  return (
    <Box m="20px">
      <Header
        title="Object Details"
        subtitle={"on: " + state.objectdetails}
      ></Header>
      <Box height="75vh">
        <Table data={data} columns={columns} initialState={initialState} />
      </Box>
    </Box>
  );
};

export default ObjectDetails;
