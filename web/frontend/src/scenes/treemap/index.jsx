import React, { useState, useEffect } from "react";
import { Box } from "@mui/material";
import Header from "../../components/Header";
import { propertyValues as data } from "../../data/procontentData";
import { BACKEND_URL } from "../../AppConfig";
import TreeMap from "../../components/TreeMap";
import { useSessionStorage } from "@uidotdev/usehooks";

const Treemap = () => {
  const [filter, setFilter] = useSessionStorage("filterString", "");
  const [data, setData] = useState({});
  useEffect(() => {
    console.log("updating bar chart");
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const fetchPost = async () => {
      try {
        var raw = JSON.stringify({});

        var requestOptions = {
          method: "POST",
          headers: myHeaders,
          body: raw,
          redirect: "follow",
        };

        const response = await fetch(
          BACKEND_URL +
            "/samplinginfo?" +
            new URLSearchParams({
              properties: ["FORMAT", "MIMETYPE"],
              algorithm: "SELECTIVE_FEATURE_DISTRIBUTION",
              filter: filter,
            }),
          requestOptions
        );
        const res = await response.json();

        var data = {
          name: "collection",
          children: [],
        };
        var children = [];
        res.forEach(function (item, index) {
          children.push({
            value: parseInt(item[0], 10),
            name: item[2] + " & " + item[3],
          });
        });
        data["children"] = children;
        console.log(data);
        setData(data);
      } catch (error) {
        console.log(error);
      }
    };
    fetchPost();
  }, [filter]);

  return (
    <Box m="20px">
      <Header title="Bar Chart" subtitle="TreeMap"></Header>
      <Box height="75vh">
        <TreeMap treemapdata={data} />
      </Box>
    </Box>
  );
};

export default Treemap;
