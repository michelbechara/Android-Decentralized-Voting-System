pragma solidity ^0.4.0;

contract VotingSystem { //Form the candidate
    struct Candidate{
      uint id;
      string candidate_name;
      uint sumvotes;
      bool isMale;
    }

    //in this mapping we store the accounts who have voted
    mapping(address => bool) public voters;
    mapping(uint => Candidate) public candidates; //Key-value pair (uint id as key)

    uint public candidatesNumber; //how many candidates there are

    constructor() public{ //constructor
      //addCandidate("Mira" );
      //addCandidate("Jad");
      //addCandidate("Michel");
      //addCandidate("Laura");
    }

    function addCandidate (string candidate_name, bool isMale) public{
      candidatesNumber++; //increment the number of candidates
      candidates[candidatesNumber]=Candidate(candidatesNumber, candidate_name, 0, isMale);
    }

    function removeCandidate (uint id) public{
      delete candidates[id];
    }

    function modifyCandidate (uint id, string candidate_name, bool isMale) public{
      //candidates[id]=Candidate(id, candidate_name)
      uint sumvotes = candidates[id].sumvotes;
      candidates[id]= Candidate(id, candidate_name, sumvotes,isMale);
    }

    function vote(uint _candidateId) public{
        //adress has not voted before
        require(!voters[msg.sender]); //not in the mapping => has not voted before
        //valid candidate
        require(_candidateId > 0 && _candidateId<=candidatesNumber);
        /*Know and save that this account has voted. Solidity allows us to know who is sending
        this function through meta data (msg.sender)*/
        voters[msg.sender] = true;
        //increment vote count for correct candidate
        candidates[_candidateId].sumvotes ++;
    }
}
