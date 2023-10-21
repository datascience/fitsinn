import { Box } from "@mui/material";
import Header from "../../components/Header";
import Table from "../../components/Table";
import React, { useState, useEffect } from "react";
import { useTracked } from "react-tracked";
import { BACKEND_URL } from "../../AppConfig";
import { useNavigate } from "react-router-dom";

const Objects = () => {
  const [data, setData] = useState([
    {
      id: 1,
      count: 1,
      filepath: "/usr/local/tomcat/webapps/fits/upload/1582118786085/README.md",
    },
  ]);
  const [state, dispatch] = useTracked();
  const navigate = useNavigate();
  useEffect(() => {
    console.log("loading the object list");
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const fetchPost = async () => {
      try {
        var raw = JSON.stringify({
          filter: state.filter,
        });

        var requestOptions = {
          method: "POST",
          headers: myHeaders,
          body: raw,
          redirect: "follow",
        };

        const response = await fetch(
          BACKEND_URL +
            "/objects?" +
            new URLSearchParams({
              filter: state.filter,
            }),
          requestOptions
        );
        const data = await response.json();
        let id = 0;
        var tmp = data.map((stat) => ({
          id: id++,
          count: stat.count,
          filepath: stat.filepath,
        }));

        setData(tmp);
      } catch (error) {
        console.log(error);
      }
    };
    fetchPost();
  }, [state.filter]);

  const initialState = {
    sorting: {
      sortModel: [{ field: "filepath", sort: "asc" }],
    },
  };

  const columns = [
    {
      field: "filepath",
      headerName: "File Path",
      flex: 0.5,
    },
    {
      field: "count",
      headerName: "Property Count",
      flex: 0.5,
    },
  ];

  const handleRowClick = (params) => {
    console.log(params.row.filepath);
    dispatch({ objectdetails: params.row.filepath });
    navigate(`/objectdetails`);
  };

  return (
    <Box m="20px">
      <Header
        title="Digital Objects"
        subtitle="List of the objects matching Filter"
      ></Header>
      <Box height="75vh">
        <Table
          data={data}
          columns={columns}
          initialState={initialState}
          onRowClick={handleRowClick}
        />
      </Box>
    </Box>
  );
};

export default Objects;
